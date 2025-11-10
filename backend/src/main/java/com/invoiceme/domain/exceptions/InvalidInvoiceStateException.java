package com.invoiceme.domain.exceptions;

/**
 * Thrown when an invalid invoice state transition is attempted.
 */
public class InvalidInvoiceStateException extends DomainValidationException {
    
    public InvalidInvoiceStateException(String message) {
        super(message);
    }
}


