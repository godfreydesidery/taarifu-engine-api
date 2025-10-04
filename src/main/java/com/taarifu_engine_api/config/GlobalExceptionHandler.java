package com.taarifu_engine_api.config;

import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Taarifu Engine API.
 * Handles all exceptions thrown by controllers and services.
 * 
 * @author Taarifu Team
 * @version 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ApiException - our custom application exceptions
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleApiException(ApiException ex) {
        log.warn("API Exception occurred: {} - Status: {}", ex.getMessage(), ex.getStatusCode());
        
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                false,
                ex.getStatusCode().value(),
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ResponseWrapper<Map<String, String>> response = new ResponseWrapper<>(
                false,
                400,
                "Validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle malformed JSON requests
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        
        log.warn("Malformed JSON request: {}", ex.getMessage());
        
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                false,
                400,
                "Invalid JSON format in request body",
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle all other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                false,
                500,
                "An unexpected error occurred. Please try again later.",
                null
        );

        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * Handle IllegalArgumentException - common runtime exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                false,
                400,
                "Invalid request parameters: " + ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle SecurityException - access control violations
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleSecurityException(SecurityException ex) {
        log.warn("Security violation: {}", ex.getMessage());
        
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                false,
                403,
                "Access denied: " + ex.getMessage(),
                null
        );

        return ResponseEntity.status(403).body(response);
    }
}
