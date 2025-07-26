package com.survey.repository;

import com.survey.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides data access methods for user management.
 * 
 * @author Survey Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if username exists.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by role.
     * 
     * @param role the role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(User.UserRole role);
    
    /**
     * Find active users.
     * 
     * @return List of active users
     */
    List<User> findByActiveTrue();
    
    /**
     * Find users by role and active status.
     * 
     * @param role the role to filter by
     * @param active the active status to filter by
     * @return List of users matching the criteria
     */
    List<User> findByRoleAndActive(User.UserRole role, boolean active);
    
    /**
     * Count users by role.
     * 
     * @param role the role to count
     * @return number of users with the specified role
     */
    long countByRole(User.UserRole role);
    
    /**
     * Find admin users.
     * 
     * @return List of admin users
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.active = true")
    List<User> findActiveAdmins();
    
    /**
     * Find users who haven't logged in recently.
     * 
     * @param days number of days to check
     * @return List of users who haven't logged in within the specified days
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL OR u.lastLogin < :days")
    List<User> findUsersNotLoggedInRecently(@Param("days") java.time.LocalDateTime days);
    
    /**
     * Search users by username or email containing the search term.
     * 
     * @param username the username search term
     * @param email the email search term
     * @return List of users matching the search criteria
     */
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
} 