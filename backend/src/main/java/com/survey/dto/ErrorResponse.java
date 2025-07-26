package com.survey.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Standardized error response DTO for consistent error handling.
 */
public class ErrorResponse {
    
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final String errorId;
    private final String errorCode;
    private final Map<String, String> fieldErrors;
    private final String traceId;
    
    public ErrorResponse(Builder builder) {
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.error = builder.error;
        this.message = builder.message;
        this.path = builder.path;
        this.errorId = builder.errorId;
        this.errorCode = builder.errorCode;
        this.fieldErrors = builder.fieldErrors;
        this.traceId = builder.traceId;
    }
    
    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public String getErrorId() { return errorId; }
    public String getErrorCode() { return errorCode; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public String getTraceId() { return traceId; }
    
    /**
     * Builder class for ErrorResponse.
     */
    public static class Builder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private int status;
        private String error;
        private String message;
        private String path;
        private String errorId;
        private String errorCode;
        private Map<String, String> fieldErrors = new HashMap<>();
        private String traceId;
        
        public Builder status(int status) {
            this.status = status;
            return this;
        }
        
        public Builder error(String error) {
            this.error = error;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder path(String path) {
            this.path = path;
            return this;
        }
        
        public Builder errorId(String errorId) {
            this.errorId = errorId;
            return this;
        }
        
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder fieldErrors(Map<String, String> fieldErrors) {
            this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
            return this;
        }
        
        public Builder addFieldError(String field, String error) {
            this.fieldErrors.put(field, error);
            return this;
        }
        
        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }
        
        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
} 