package com.secland.centralbank.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.secland.centralbank.exception.AccountNotFoundException;
import com.secland.centralbank.exception.InsufficientFundsException;
import com.secland.centralbank.exception.InvalidAccountTypeException;
import com.secland.centralbank.exception.UserNotFoundException;

/**
 * Global exception handler for the banking application.
 * <p>
 * Provides centralized error handling for all controllers, ensuring consistent
 * error responses across the API. Includes proper logging and security considerations.
 * </p>
 * 
 * <p>
 * Features:
 * - Structured error responses with timestamps
 * - Comprehensive logging for debugging
 * - Security-aware error messages (no sensitive data exposure)
 * - Validation error handling
 * - Business logic exception handling
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Error response structure for consistent API error formatting.
     */
    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final String path;

        public ErrorResponse(int status, String error, String message, String path) {
            this.timestamp = LocalDateTime.now();
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
    }

    /**
     * Handles validation errors from @Valid annotations.
     * 
     * @param ex the validation exception
     * @param request the web request
     * @return structured error response with field-specific validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("message", "Request validation failed");
        response.put("fieldErrors", fieldErrors);
        response.put("path", request.getDescription(false));

        // log.warn("Validation error: {} - Fields: {}", ex.getMessage(), fieldErrors.keySet());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles business logic exceptions for account operations.
     * 
     * @param ex the account-related exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
            AccountNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Account Not Found",
            ex.getMessage(),
            request.getDescription(false)
        );

        log.warn("Account not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles insufficient funds exceptions for transfer operations.
     * 
     * @param ex the insufficient funds exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(
            InsufficientFundsException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Insufficient Funds",
            ex.getMessage(),
            request.getDescription(false)
        );

        log.warn("Insufficient funds: {}", ex.getMessage());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles invalid account type exceptions.
     * 
     * @param ex the invalid account type exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(InvalidAccountTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountTypeException(
            InvalidAccountTypeException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Account Type",
            ex.getMessage(),
            request.getDescription(false)
        );

        log.warn("Invalid account type: {}", ex.getMessage());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles user not found exceptions.
     * 
     * @param ex the user not found exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "User Not Found",
            ex.getMessage(),
            request.getDescription(false)
        );

        log.warn("User not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles authentication exceptions.
     * 
     * @param ex the authentication exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication Failed",
            "Invalid credentials or authentication token",
            request.getDescription(false)
        );

        log.warn("Authentication failed: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles access denied exceptions.
     * 
     * @param ex the access denied exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Access Denied",
            "You do not have permission to access this resource",
            request.getDescription(false)
        );

        log.warn("Access denied: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handles data integrity violations, such as duplicate key constraints.
     * 
     * @param ex the data integrity violation exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        String message = "Data integrity violation occurred";
        
        // Check if it's a duplicate key violation for username
        if (ex.getMessage() != null && ex.getMessage().contains("duplicate key value violates unique constraint")) {
            if (ex.getMessage().contains("username")) {
                message = "Username already exists. Please choose a different username.";
            } else {
                message = "A record with this information already exists.";
            }
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            message,
            request.getDescription(false)
        );

        log.warn("Data integrity violation: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles all other unexpected exceptions.
     * 
     * @param ex the unexpected exception
     * @param request the web request
     * @return structured error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false)
        );

        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 