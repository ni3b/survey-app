import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { errorService, ErrorType, ErrorSeverity, ErrorContext } from '../services/errorService';
import { useAuth } from '../context/AuthContext';

/**
 * Custom hook for comprehensive error handling in React components
 */
export const useErrorHandler = (componentName?: string) => {
  const navigate = useNavigate();
  const { logout } = useAuth();

  /**
   * Handle errors with automatic categorization and user notification
   */
  const handleError = useCallback((
    error: any,
    context?: Omit<ErrorContext, 'component'>,
    showToast: boolean = true
  ) => {
    const fullContext: ErrorContext = {
      component: componentName,
      ...context
    };

    const appError = errorService.handleError(error, fullContext, showToast);

    // Handle specific error types
    switch (appError.type) {
      case ErrorType.AUTHENTICATION:
        // Redirect to login on authentication errors
        logout();
        navigate('/login');
        break;
      
      case ErrorType.AUTHORIZATION:
        // Redirect to home on authorization errors
        navigate('/');
        break;
      
      case ErrorType.NETWORK:
        // For network errors, we might want to show a retry option
        // This could be handled by the component itself
        break;
      
      default:
        // For other errors, let the component handle them
        break;
    }

    return appError;
  }, [componentName, logout, navigate]);

  /**
   * Handle async operations with automatic error handling
   */
  const handleAsync = useCallback(async <T>(
    asyncFn: () => Promise<T>,
    context?: Omit<ErrorContext, 'component'>,
    showToast: boolean = true
  ): Promise<T | null> => {
    try {
      return await asyncFn();
    } catch (error) {
      handleError(error, context, showToast);
      return null;
    }
  }, [handleError]);

  /**
   * Handle form submission errors specifically
   */
  const handleFormError = useCallback((
    error: any,
    formName?: string,
    showToast: boolean = true
  ) => {
    const context: ErrorContext = {
      component: componentName,
      action: 'form_submission',
      additionalData: { formName }
    };

    return handleError(error, context, showToast);
  }, [componentName, handleError]);

  /**
   * Handle API call errors
   */
  const handleApiError = useCallback((
    error: any,
    endpoint?: string,
    method?: string,
    showToast: boolean = true
  ) => {
    const context: ErrorContext = {
      component: componentName,
      action: 'api_call',
      additionalData: { endpoint, method }
    };

    return handleError(error, context, showToast);
  }, [componentName, handleError]);

  /**
   * Handle validation errors
   */
  const handleValidationError = useCallback((
    error: any,
    fieldName?: string,
    showToast: boolean = true
  ) => {
    const context: ErrorContext = {
      component: componentName,
      action: 'validation',
      additionalData: { fieldName }
    };

    return handleError(error, context, showToast);
  }, [componentName, handleError]);

  /**
   * Create a safe async function wrapper
   */
  const createSafeAsync = useCallback(<T extends any[], R>(
    asyncFn: (...args: T) => Promise<R>,
    context?: Omit<ErrorContext, 'component'>,
    showToast: boolean = true
  ) => {
    return async (...args: T): Promise<R | null> => {
      try {
        return await asyncFn(...args);
      } catch (error) {
        handleError(error, context, showToast);
        return null;
      }
    };
  }, [handleError]);

  return {
    handleError,
    handleAsync,
    handleFormError,
    handleApiError,
    handleValidationError,
    createSafeAsync
  };
};

/**
 * Hook for handling errors in forms with field-specific error handling
 */
export const useFormErrorHandler = (componentName?: string) => {
  const { handleError } = useErrorHandler(componentName);

  const handleFieldError = useCallback((
    error: any,
    fieldName: string,
    showToast: boolean = false
  ) => {
    const context: ErrorContext = {
      component: componentName,
      action: 'field_validation',
      additionalData: { fieldName }
    };

    return handleError(error, context, showToast);
  }, [componentName, handleError]);

  const handleFormSubmissionError = useCallback((
    error: any,
    formName?: string,
    showToast: boolean = true
  ) => {
    const context: ErrorContext = {
      component: componentName,
      action: 'form_submission',
      additionalData: { formName }
    };

    return handleError(error, context, showToast);
  }, [componentName, handleError]);

  return {
    handleFieldError,
    handleFormSubmissionError
  };
};

/**
 * Hook for handling errors in data fetching operations
 */
export const useDataErrorHandler = (componentName?: string) => {
  const { handleError } = useErrorHandler(componentName);

  const handleFetchError = useCallback((
    error: any,
    dataType?: string,
    showToast: boolean = true
  ) => {
    const context: ErrorContext = {
      component: componentName,
      action: 'data_fetch',
      additionalData: { dataType }
    };

    return handleError(error, context, showToast);
  }, [componentName, handleError]);

  const handleSaveError = useCallback((
    error: any,
    dataType?: string,
    operation?: 'create' | 'update' | 'delete',
    showToast: boolean = true
  ) => {
    const context: ErrorContext = {
      component: componentName,
      action: 'data_save',
      additionalData: { dataType, operation }
    };

    return handleError(error, context, showToast);
  }, [componentName, handleError]);

  return {
    handleFetchError,
    handleSaveError
  };
}; 