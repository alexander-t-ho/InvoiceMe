package com.invoiceme.api.exceptions;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.exceptions.InsufficientPaymentException;
import com.invoiceme.domain.exceptions.InvalidInvoiceStateException;
import com.invoiceme.domain.exceptions.InvalidLineItemException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 * Maps domain exceptions to appropriate HTTP status codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DomainValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDomainValidationException(
            DomainValidationException ex,
            HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(InvalidInvoiceStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidInvoiceStateException(
            InvalidInvoiceStateException ex,
            HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Invoice State",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(InsufficientPaymentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInsufficientPaymentException(
            InsufficientPaymentException ex,
            HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Insufficient Payment",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(InvalidLineItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidLineItemException(
            InvalidLineItemException ex,
            HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Line Item",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Argument",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Request validation failed");
        response.put("errors", errors);
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Constraint Violation",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        // Log the exception for debugging
        ex.printStackTrace();
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

