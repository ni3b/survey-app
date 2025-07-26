package com.survey.controller;

import com.survey.dto.UserDto;
import com.survey.model.User;
import com.survey.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User management controller for admin operations.
 * Provides endpoints for creating, updating, and managing users.
 * 
 * @author Survey Team
 */
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "User Management", description = "Admin endpoints for user management")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Get all users.
     * 
     * @return list of all users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Get a list of all users in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user by ID.
     * 
     * @param id the user ID
     * @return user details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get detailed information about a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID") 
            @PathVariable Long id) {
        return userService.findById(id)
                .map(UserDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new user.
     * 
     * @param userDto the user data
     * @return the created user
     */
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created user"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            // Check if username already exists
            if (userService.usernameExists(userDto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username already exists"));
            }
            
            // Check if email already exists
            if (userDto.getEmail() != null && userService.emailExists(userDto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already exists"));
            }
            
            User createdUser = userService.createUserFromDto(userDto);
            return ResponseEntity.ok(new UserDto(createdUser));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update an existing user.
     * 
     * @param id the user ID
     * @param userDto the updated user data
     * @return the updated user
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID") 
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {
        try {
            User updatedUser = userService.updateUserFromDto(id, userDto);
            return ResponseEntity.ok(new UserDto(updatedUser));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a user.
     * 
     * @param id the user ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID") 
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Change user role.
     * 
     * @param id the user ID
     * @param request the role change request
     * @return success response
     */
    @PutMapping("/{id}/role")
    @Operation(summary = "Change user role", description = "Change a user's role (USER/ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully changed user role"),
        @ApiResponse(responseCode = "400", description = "Invalid role"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<?> changeUserRole(
            @Parameter(description = "User ID") 
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String roleStr = request.get("role");
            if (roleStr == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Role is required"));
            }
            
            User.UserRole role = User.UserRole.valueOf(roleStr.toUpperCase());
            boolean success = userService.changeUserRole(id, role);
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "User role changed successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role. Must be USER or ADMIN"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Activate or deactivate user.
     * 
     * @param id the user ID
     * @param request the activation request
     * @return success response
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Change user status", description = "Activate or deactivate a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully changed user status"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<?> changeUserStatus(
            @Parameter(description = "User ID") 
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean active = request.get("active");
            if (active == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Active status is required"));
            }
            
            boolean success = userService.setUserActive(id, active);
            
            if (success) {
                String message = active ? "User activated successfully" : "User deactivated successfully";
                return ResponseEntity.ok(Map.of("message", message));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get users by role.
     * 
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Get all users with a specific role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "400", description = "Invalid role"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<?> getUsersByRole(
            @Parameter(description = "User role (USER/ADMIN)") 
            @PathVariable String role) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            List<UserDto> users = userService.getUsersByRole(userRole).stream()
                    .map(UserDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role. Must be USER or ADMIN"));
        }
    }
    
    /**
     * Search users by username or email.
     * 
     * @param searchTerm the search term
     * @return list of matching users
     */
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by username or email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<List<UserDto>> searchUsers(
            @Parameter(description = "Search term for username or email") 
            @RequestParam String searchTerm) {
        List<UserDto> users = userService.searchUsers(searchTerm).stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user statistics.
     * 
     * @return user statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get user statistics", description = "Get user statistics and counts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics"),
        @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> stats = Map.of(
            "totalUsers", userService.getAllUsers().size(),
            "adminUsers", userService.getAdminUsers().size(),
            "activeUsers", userService.getActiveUsers().size(),
            "userCount", userService.getUserCountByRole(User.UserRole.USER),
            "adminCount", userService.getUserCountByRole(User.UserRole.ADMIN)
        );
        return ResponseEntity.ok(stats);
    }
} 