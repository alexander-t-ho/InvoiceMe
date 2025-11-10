package com.invoiceme.domain.events;

import java.util.UUID;

/**
 * Domain event published when an invoice is created.
 */
public class InvoiceCreatedEvent {
    
    private final UUID invoiceId;
    private final UUID customerId;
    
    public InvoiceCreatedEvent(UUID invoiceId, UUID customerId) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
    }
    
    public UUID getInvoiceId() {
        return invoiceId;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
}


