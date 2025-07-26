import { toast } from 'react-toastify';

/**
 * Error types for different categories of errors
 */
export enum ErrorType {
  NETWORK = 'NETWORK',
  AUTHENTICATION = 'AUTHENTICATION',
  AUTHORIZATION = 'AUTHORIZATION',
  VALIDATION = 'VALIDATION',
  NOT_FOUND = 'NOT_FOUND',
  BUSINESS_LOGIC = 'BUSINESS_LOGIC',
  UNKNOWN = 'UNKNOWN'
}

/**
 * Error severity levels
 */
export enum ErrorSeverity {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

/**
 * Standardized error interface
 */
export interface AppError {
  type: ErrorType;
  severity: ErrorSeverity;
  message: string;
  userMessage: string;
  errorId?: string;
  errorCode?: string;
  fieldErrors?: Record<string, string>;
  timestamp: Date;
  originalError?: any;
}

/**
 * Error context for additional information
 */
export interface ErrorContext {
  component?: string;
  action?: string;
  userId?: string;
  requestId?: string;
  additionalData?: Record<string, any>;
}

/**
 * Error handling service for comprehensive error management
 */
class ErrorService {
  private errorLog: AppError[] = [];
  private readonly maxLogSize = 100;

  /**
   * Create a standardized error from various error sources
   */
  createError(
    error: any,
    type: ErrorType = ErrorType.UNKNOWN,
    severity: ErrorSeverity = ErrorSeverity.MEDIUM,
    context?: ErrorContext
  ): AppError {
    const appError: AppError = {
      type,
      severity,
      message: this.extractErrorMessage(error),
      userMessage: this.getUserFriendlyMessage(error, type),
      errorId: this.extractErrorId(error),
      errorCode: this.extractErrorCode(error),
      fieldErrors: this.extractFieldErrors(error),
      timestamp: new Date(),
      originalError: error
    };

    this.logError(appError, context);
    return appError;
  }

  /**
   * Handle API errors with proper categorization
   */
  handleApiError(error: any, context?: ErrorContext): AppError {
    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;

      let type = ErrorType.UNKNOWN;
      let severity = ErrorSeverity.MEDIUM;

      switch (status) {
        case 400:
          type = data.fieldErrors ? ErrorType.VALIDATION : ErrorType.BUSINESS_LOGIC;
          severity = ErrorSeverity.LOW;
          break;
        case 401:
          type = ErrorType.AUTHENTICATION;
          severity = ErrorSeverity.MEDIUM;
          break;
        case 403:
          type = ErrorType.AUTHORIZATION;
          severity = ErrorSeverity.MEDIUM;
          break;
        case 404:
          type = ErrorType.NOT_FOUND;
          severity = ErrorSeverity.LOW;
          break;
        case 500:
          type = ErrorType.UNKNOWN;
          severity = ErrorSeverity.HIGH;
          break;
        default:
          type = ErrorType.UNKNOWN;
          severity = ErrorSeverity.MEDIUM;
      }

      return this.createError(error, type, severity, context);
    } else if (error.request) {
      // Network error
      return this.createError(error, ErrorType.NETWORK, ErrorSeverity.HIGH, context);
    } else {
      // Other error
      return this.createError(error, ErrorType.UNKNOWN, ErrorSeverity.MEDIUM, context);
    }
  }

  /**
   * Show error to user via toast notification
   */
  showError(error: AppError, showToast: boolean = true): void {
    if (showToast) {
      const toastType = this.getToastType(error.severity);
      toast.error(error.userMessage, {
        position: 'top-right',
        autoClose: this.getToastDuration(error.severity),
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
      });
    }

    // Log to console for debugging
    console.error('Application Error:', {
      type: error.type,
      severity: error.severity,
      message: error.message,
      userMessage: error.userMessage,
      errorId: error.errorId,
      errorCode: error.errorCode,
      fieldErrors: error.fieldErrors,
      timestamp: error.timestamp,
      originalError: error.originalError
    });
  }

  /**
   * Handle error with automatic user notification
   */
  handleError(error: any, context?: ErrorContext, showToast: boolean = true): AppError {
    const appError = this.handleApiError(error, context);
    this.showError(appError, showToast);
    return appError;
  }

  /**
   * Get all logged errors
   */
  getErrorLog(): AppError[] {
    return [...this.errorLog];
  }

  /**
   * Clear error log
   */
  clearErrorLog(): void {
    this.errorLog = [];
  }

  /**
   * Extract error message from various error types
   */
  private extractErrorMessage(error: any): string {
    if (error.response?.data?.message) {
      return error.response.data.message;
    }
    if (error.message) {
      return error.message;
    }
    if (typeof error === 'string') {
      return error;
    }
    return 'An unknown error occurred';
  }

  /**
   * Extract error ID from API response
   */
  private extractErrorId(error: any): string | undefined {
    return error.response?.data?.errorId;
  }

  /**
   * Extract error code from API response
   */
  private extractErrorCode(error: any): string | undefined {
    return error.response?.data?.errorCode;
  }

  /**
   * Extract field errors from validation response
   */
  private extractFieldErrors(error: any): Record<string, string> | undefined {
    return error.response?.data?.fieldErrors || error.response?.data?.errors;
  }

  /**
   * Get user-friendly error message
   */
  private getUserFriendlyMessage(error: any, type: ErrorType): string {
    const defaultMessage = 'Something went wrong. Please try again.';

    if (error.response?.data?.message) {
      return error.response.data.message;
    }

    switch (type) {
      case ErrorType.NETWORK:
        return 'Network error. Please check your connection and try again.';
      case ErrorType.AUTHENTICATION:
        return 'Authentication failed. Please log in again.';
      case ErrorType.AUTHORIZATION:
        return 'You don\'t have permission to perform this action.';
      case ErrorType.VALIDATION:
        return 'Please check your input and try again.';
      case ErrorType.NOT_FOUND:
        return 'The requested resource was not found.';
      case ErrorType.BUSINESS_LOGIC:
        return error.message || 'This action cannot be completed.';
      default:
        return defaultMessage;
    }
  }

  /**
   * Get toast type based on error severity
   */
  private getToastType(severity: ErrorSeverity): 'error' | 'warning' | 'info' {
    switch (severity) {
      case ErrorSeverity.CRITICAL:
      case ErrorSeverity.HIGH:
        return 'error';
      case ErrorSeverity.MEDIUM:
        return 'warning';
      case ErrorSeverity.LOW:
        return 'info';
      default:
        return 'error';
    }
  }

  /**
   * Get toast duration based on error severity
   */
  private getToastDuration(severity: ErrorSeverity): number {
    switch (severity) {
      case ErrorSeverity.CRITICAL:
        return 10000; // 10 seconds
      case ErrorSeverity.HIGH:
        return 8000;  // 8 seconds
      case ErrorSeverity.MEDIUM:
        return 5000;  // 5 seconds
      case ErrorSeverity.LOW:
        return 3000;  // 3 seconds
      default:
        return 5000;
    }
  }

  /**
   * Log error with context
   */
  private logError(error: AppError, context?: ErrorContext): void {
    const logEntry = {
      ...error,
      context
    };

    this.errorLog.push(logEntry);

    // Keep log size manageable
    if (this.errorLog.length > this.maxLogSize) {
      this.errorLog = this.errorLog.slice(-this.maxLogSize);
    }

    // Log to external service in production
    if (process.env.NODE_ENV === 'production') {
      this.logToExternalService(logEntry);
    }
  }

  /**
   * Log to external service (placeholder for production logging)
   */
  private logToExternalService(logEntry: any): void {
    // In production, this would send to a logging service like Sentry, LogRocket, etc.
    console.log('External logging:', logEntry);
  }
}

// Export singleton instance
export const errorService = new ErrorService();

// Export convenience functions
export const handleError = (error: any, context?: ErrorContext, showToast?: boolean) => 
  errorService.handleError(error, context, showToast);

export const createError = (error: any, type?: ErrorType, severity?: ErrorSeverity, context?: ErrorContext) => 
  errorService.createError(error, type, severity, context);

export const showError = (error: AppError, showToast?: boolean) => 
  errorService.showError(error, showToast); 