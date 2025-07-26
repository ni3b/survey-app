package com.survey.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.survey.exception.SurveyAuthenticationException;
import com.survey.exception.BusinessException;
import com.survey.exception.ResourceNotFoundException;
import com.survey.exception.ValidationException;
import com.survey.exception.UnauthorizedException;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for the Survey Application.
 * Centralizes error handling and provides consistent error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Validation error occurred - ID: {}, Path: {}, Errors: {}", 
                   errorId, request.getDescription(false), ex.getBindingResult().getAllErrors());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("message", "Invalid input data");
        response.put("errors", errors);
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle validation exceptions.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        logger.warn("Validation error - ID: {}, Path: {}, Message: {}, Field Errors: {}", 
                   ex.getErrorId(), request.getDescription(false), ex.getMessage(), ex.getFieldErrors());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", ex.getTimestamp());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false));
        response.put("errorId", ex.getErrorId());
        response.put("errorCode", ex.getErrorCode());
        
        if (ex.hasFieldErrors()) {
            response.put("fieldErrors", ex.getFieldErrors());
        }

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle constraint violation exceptions.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Constraint violation occurred - ID: {}, Path: {}, Message: {}", 
                   errorId, request.getDescription(false), ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Constraint Violation");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle resource not found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        
        logger.warn("Resource not found - ID: {}, Path: {}, Message: {}", 
                   ex.getErrorId(), request.getDescription(false), ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", ex.getTimestamp());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false));
        response.put("errorId", ex.getErrorId());
        response.put("errorCode", ex.getErrorCode());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle unauthorized access exceptions.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Unauthorized access - ID: {}, Path: {}, Message: {}", 
                   errorId, request.getDescription(false), ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Unauthorized");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle authentication exceptions.
     */
    @ExceptionHandler({SurveyAuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            Exception ex, WebRequest request) {
        
        String errorId = ex instanceof SurveyAuthenticationException ? 
            ((SurveyAuthenticationException) ex).getErrorId() : UUID.randomUUID().toString();
        
        logger.warn("Authentication failed - ID: {}, Path: {}, Message: {}", 
                   errorId, request.getDescription(false), ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Authentication Failed");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Access denied - ID: {}, Path: {}, Message: {}", 
                   errorId, request.getDescription(false), ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Access Denied");
        response.put("message", "You don't have permission to access this resource");
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle method argument type mismatch exceptions.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Method argument type mismatch - ID: {}, Path: {}, Parameter: {}, Value: {}", 
                   errorId, request.getDescription(false), ex.getName(), ex.getValue());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Parameter");
        response.put("message", "Invalid parameter type for: " + ex.getName());
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle business logic exceptions.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        logger.warn("Business logic error - ID: {}, Path: {}, Message: {}", 
                   ex.getErrorId(), request.getDescription(false), ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", ex.getTimestamp());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Business Error");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false));
        response.put("errorId", ex.getErrorId());
        response.put("errorCode", ex.getErrorCode());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle general runtime exceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.error("Runtime exception occurred - ID: {}, Path: {}, Message: {}", 
                    errorId, request.getDescription(false), ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred");
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.error("Unhandled exception occurred - ID: {}, Path: {}, Message: {}", 
                    errorId, request.getDescription(false), ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred");
        response.put("path", request.getDescription(false));
        response.put("errorId", errorId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


} 