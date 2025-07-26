package com.survey.exception;

/**
 * Exception thrown when access is unauthorized.
 */
public class UnauthorizedException extends SurveyException {
    
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED_ACCESS");
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, "UNAUTHORIZED_ACCESS", cause);
    }
    
    public static UnauthorizedException accessDenied(String resource) {
        return new UnauthorizedException("Access denied to resource: " + resource);
    }
    
    public static UnauthorizedException insufficientPermissions(String action) {
        return new UnauthorizedException("Insufficient permissions to perform action: " + action);
    }
} 