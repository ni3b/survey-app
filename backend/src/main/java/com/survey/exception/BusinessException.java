package com.survey.exception;

/**
 * Exception thrown when business logic rules are violated.
 */
public class BusinessException extends SurveyException {
    
    public BusinessException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }
    
    public BusinessException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, "BUSINESS_RULE_VIOLATION", cause);
    }
    
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
} 