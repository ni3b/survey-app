package com.survey.exception;

import java.util.Map;
import java.util.HashMap;

/**
 * Exception thrown when data validation fails.
 */
public class ValidationException extends SurveyException {
    
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.fieldErrors = new HashMap<>();
    }
    
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, "VALIDATION_ERROR");
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }
    
    public ValidationException(String message, String field, String error) {
        super(message, "VALIDATION_ERROR");
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, error);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
        this.fieldErrors = new HashMap<>();
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
    
    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }
    
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
} 