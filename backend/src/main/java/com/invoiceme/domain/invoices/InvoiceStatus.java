package com.invoiceme.domain.invoices;

/**
 * Invoice status enumeration.
 * Represents the lifecycle states of an invoice.
 */
public enum InvoiceStatus {
    /**
     * Invoice is in draft state and can be modified.
     */
    DRAFT,
    
    /**
     * Invoice has been sent to the customer.
     * Can no longer be modified, but payments can be applied.
     */
    SENT,
    
    /**
     * Invoice has been fully paid.
     * No further modifications or payments allowed.
     */
    PAID
}


