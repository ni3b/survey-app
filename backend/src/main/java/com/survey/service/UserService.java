package com.survey.service;

import com.survey.exception.SurveyAuthenticationException;
import com.survey.exception.BusinessException;
import com.survey.exception.ResourceNotFoundException;
import com.survey.exception.ValidationException;
import com.survey.model.User;
import com.survey.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service class for user management and authentication.
 * 
 * @author Survey Team
 */
@Service
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", username));
            
            if (!user.isActive()) {
                logger.warn("Inactive user attempted login: {}", username);
                throw SurveyAuthenticationException.userInactive(username);
            }
            
            logger.debug("User loaded successfully: {} with role: {}", username, user.getRole());
            
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isActive(),
                    true, true, true,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username, e);
        }
    }
    
    /**
     * Find user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by ID.
     * 
     * @param id the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Create a new user.
     * 
     * @param user the user to create
     * @return the created user
     */
    public User createUser(User user) {
        logger.info("Creating new user: {}", user.getUsername());
        
        try {
            // Validate user data
            validateUserData(user);
            
            // Check if username already exists
            if (usernameExists(user.getUsername())) {
                throw new BusinessException("Username already exists: " + user.getUsername());
            }
            
            // Check if email already exists (if provided)
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                if (emailExists(user.getEmail())) {
                    throw new BusinessException("Email already exists: " + user.getEmail());
                }
            }
            
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            logger.debug("Password encoded for user: {}", user.getUsername());
            
            // Set default role if not specified
            if (user.getRole() == null) {
                user.setRole(User.UserRole.USER);
                logger.debug("Default role USER assigned to user: {}", user.getUsername());
            }
            
            User savedUser = userRepository.save(user);
            logger.info("User created successfully: {} with role: {}", savedUser.getUsername(), savedUser.getRole());
            
            return savedUser;
        } catch (Exception e) {
            logger.error("Failed to create user: {} - {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Update user information.
     * 
     * @param user the user to update
     * @return the updated user
     */
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update fields
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setActive(user.isActive());
        
        // Update password only if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Delete user by ID.
     * 
     * @param id the user ID to delete
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Get all users.
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get all admin users.
     * 
     * @return list of admin users
     */
    public List<User> getAdminUsers() {
        return userRepository.findByRole(User.UserRole.ADMIN);
    }
    
    /**
     * Get all active users.
     * 
     * @return list of active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }
    
    /**
     * Update user's last login time.
     * 
     * @param username the username
     */
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }
    
    /**
     * Check if username exists.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Authenticate user with username and password.
     * 
     * @param username the username
     * @param password the password
     * @return Optional containing the user if authentication successful
     */
    public Optional<User> authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .filter(User::isActive);
    }
    
    /**
     * Change user password.
     * 
     * @param userId the user ID
     * @param newPassword the new password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(Long userId, String newPassword) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Activate or deactivate user.
     * 
     * @param userId the user ID
     * @param active the active status
     * @return true if status changed successfully, false otherwise
     */
    public boolean setUserActive(Long userId, boolean active) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setActive(active);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Get user count by role.
     * 
     * @param role the role to count
     * @return number of users with the specified role
     */
    public long getUserCountByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }
    
    /**
     * Create a new user with DTO.
     * 
     * @param userDto the user DTO
     * @return the created user
     */
    public User createUserFromDto(com.survey.dto.UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setRole(User.UserRole.valueOf(userDto.getRole().toUpperCase()));
        user.setActive(userDto.isActive());
        
        return createUser(user);
    }
    
    /**
     * Update user from DTO.
     * 
     * @param id the user ID
     * @param userDto the user DTO
     * @return the updated user
     */
    public User updateUserFromDto(Long id, com.survey.dto.UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRole(User.UserRole.valueOf(userDto.getRole().toUpperCase()));
        existingUser.setActive(userDto.isActive());
        
        // Update password only if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Change user role.
     * 
     * @param userId the user ID
     * @param role the new role
     * @return true if role changed successfully, false otherwise
     */
    public boolean changeUserRole(Long userId, User.UserRole role) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setRole(role);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Get users by role.
     * 
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Search users by username or email.
     * 
     * @param searchTerm the search term
     * @return list of matching users
     */
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm);
    }
    
    /**
     * Validate user data before creation or update.
     * 
     * @param user the user to validate
     * @throws ValidationException if validation fails
     */
    private void validateUserData(User user) {
        ValidationException validationException = new ValidationException("User validation failed");
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            validationException.addFieldError("username", "Username is required");
        } else if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            validationException.addFieldError("username", "Username must be between 3 and 50 characters");
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            validationException.addFieldError("password", "Password is required");
        } else if (user.getPassword().length() < 6) {
            validationException.addFieldError("password", "Password must be at least 6 characters");
        }
        
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                validationException.addFieldError("email", "Invalid email format");
            }
        }
        
        if (validationException.hasFieldErrors()) {
            throw validationException;
        }
    }
} 