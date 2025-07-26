package com.survey.controller;

import com.survey.dto.AuthDto;
import com.survey.model.User;
import com.survey.security.JwtTokenProvider;
import com.survey.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for login and user management.
 * 
 * @author Survey Team
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    /**
     * Login endpoint.
     * 
     * @param loginRequest the login request
     * @param request the HTTP request
     * @return login response with JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest loginRequest, 
                                  HttpServletRequest request) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        logger.debug("Login request from IP: {}", request.getRemoteAddr());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = tokenProvider.generateToken(authentication);
            logger.debug("JWT token generated successfully for user: {}", loginRequest.getUsername());
            
            // Update last login time
            userService.updateLastLogin(loginRequest.getUsername());
            
            User user = userService.findByUsername(loginRequest.getUsername()).orElse(null);
            
            AuthDto.LoginResponse loginResponse = new AuthDto.LoginResponse(
                jwt, 
                loginRequest.getUsername(), 
                user != null ? user.getRole().name() : "USER"
            );
            
            logger.info("Login successful for user: {} with role: {}", 
                       loginRequest.getUsername(), 
                       user != null ? user.getRole().name() : "USER");
            
            return ResponseEntity.ok(loginResponse);
            
        } catch (Exception e) {
            logger.warn("Login failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            logger.debug("Login failure details", e);
            return ResponseEntity.badRequest()
                .body(new AuthDto.LoginResponse("Invalid username or password"));
        }
    }
    

    
    /**
     * Get current user information.
     * 
     * @param request the HTTP request
     * @return current user information
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the currently authenticated user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        logger.debug("Get current user request from IP: {}", request.getRemoteAddr());
        
        try {
            String token = getJwtFromRequest(request);
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromJWT(token);
                String role = tokenProvider.getRoleFromJWT(token);
                
                logger.debug("Current user retrieved: {} with role: {}", username, role);
                return ResponseEntity.ok(new AuthDto.LoginResponse(token, username, role));
            }
            
            logger.warn("Invalid or missing token in getCurrentUser request");
            return ResponseEntity.badRequest()
                .body(new AuthDto.ErrorResponse("UNAUTHORIZED", "Invalid or missing token", 401));
                
        } catch (Exception e) {
            logger.error("Error getting current user information", e);
            return ResponseEntity.badRequest()
                .body(new AuthDto.ErrorResponse("ERROR", "Failed to get user information", 400));
        }
    }
    
    /**
     * Validate JWT token.
     * 
     * @param request the HTTP request
     * @return validation result
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        logger.debug("Token validation request from IP: {}", request.getRemoteAddr());
        
        try {
            String token = getJwtFromRequest(request);
            if (token != null && tokenProvider.validateToken(token)) {
                logger.debug("Token validation successful");
                return ResponseEntity.ok(new AuthDto.LoginResponse("Token is valid"));
            }
            
            logger.warn("Token validation failed - invalid token");
            return ResponseEntity.badRequest()
                .body(new AuthDto.LoginResponse("Invalid token"));
                
        } catch (Exception e) {
            logger.error("Token validation error", e);
            return ResponseEntity.badRequest()
                .body(new AuthDto.LoginResponse("Token validation failed"));
        }
    }
    
    /**
     * Extract JWT token from HTTP request.
     * 
     * @param request the HTTP request
     * @return JWT token string or null if not found
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 