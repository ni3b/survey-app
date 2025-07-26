package com.survey.exception;

/**
 * Exception thrown when authentication fails.
 */
public class SurveyAuthenticationException extends SurveyException {
    
    public SurveyAuthenticationException(String message) {
        super(message, "AUTHENTICATION_FAILED");
    }
    
    public SurveyAuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_FAILED", cause);
    }
    
    public static SurveyAuthenticationException invalidCredentials() {
        return new SurveyAuthenticationException("Invalid username or password");
    }
    
    public static SurveyAuthenticationException userNotFound(String username) {
        return new SurveyAuthenticationException(String.format("User '%s' not found", username));
    }
    
    public static SurveyAuthenticationException userInactive(String username) {
        return new SurveyAuthenticationException(String.format("User '%s' is inactive", username));
    }
} 