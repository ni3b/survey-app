package com.survey.exception;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base exception class for the Survey Application.
 * Provides common functionality for all custom exceptions.
 */
public abstract class SurveyException extends RuntimeException {
    
    private final String errorId;
    private final LocalDateTime timestamp;
    private final String errorCode;
    
    public SurveyException(String message) {
        super(message);
        this.errorId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.errorCode = this.getClass().getSimpleName();
    }
    
    public SurveyException(String message, Throwable cause) {
        super(message, cause);
        this.errorId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.errorCode = this.getClass().getSimpleName();
    }
    
    public SurveyException(String message, String errorCode) {
        super(message);
        this.errorId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
    }
    
    public SurveyException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
    }
    
    public String getErrorId() {
        return errorId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 