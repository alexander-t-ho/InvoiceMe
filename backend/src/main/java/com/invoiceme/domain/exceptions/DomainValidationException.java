package com.invoiceme.domain.exceptions;

/**
 * Generic domain validation exception.
 * Thrown when domain entity validation fails.
 */
public class DomainValidationException extends RuntimeException {
    
    public DomainValidationException(String message) {
        super(message);
    }
    
    public DomainValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}


