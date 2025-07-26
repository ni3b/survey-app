package com.survey.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Objects for authentication.
 * 
 * @author Survey Team
 */
public class AuthDto {
    
    /**
     * Login request DTO.
     */
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;
        
        @NotBlank(message = "Password is required")
        private String password;
        
        // Constructors
        public LoginRequest() {}
        
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        // Getters and Setters
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    /**
     * Login response DTO.
     */
    public static class LoginResponse {
        private String token;
        private String username;
        private String role;
        private String message;
        
        // Constructors
        public LoginResponse() {}
        
        public LoginResponse(String token, String username, String role) {
            this.token = token;
            this.username = username;
            this.role = role;
            this.message = "Login successful";
        }
        
        public LoginResponse(String message) {
            this.message = message;
        }
        
        // Getters and Setters
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    /**
     * Error response DTO.
     */
    public static class ErrorResponse {
        private String error;
        private String message;
        private int status;
        
        // Constructors
        public ErrorResponse() {}
        
        public ErrorResponse(String error, String message, int status) {
            this.error = error;
            this.message = message;
            this.status = status;
        }
        
        // Getters and Setters
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public int getStatus() {
            return status;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }
    }
} 