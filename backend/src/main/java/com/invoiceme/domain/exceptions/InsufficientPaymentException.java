package com.invoiceme.domain.exceptions;

/**
 * Thrown when a payment exceeds the invoice balance.
 */
public class InsufficientPaymentException extends DomainValidationException {
    
    public InsufficientPaymentException(String message) {
        super(message);
    }
}


