package com.invoiceme.domain.exceptions;

/**
 * Thrown when line item validation fails.
 */
public class InvalidLineItemException extends DomainValidationException {
    
    public InvalidLineItemException(String message) {
        super(message);
    }
}


