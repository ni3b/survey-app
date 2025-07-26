package com.survey.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends SurveyException {
    
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s with id %d not found", resource, id), "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String resource, String identifier) {
        super(String.format("%s with identifier '%s' not found", resource, identifier), "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, "RESOURCE_NOT_FOUND", cause);
    }
} 