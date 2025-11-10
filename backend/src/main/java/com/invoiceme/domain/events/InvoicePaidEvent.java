package com.invoiceme.domain.events;

import java.util.UUID;

/**
 * Domain event published when an invoice balance reaches zero (fully paid).
 */
public class InvoicePaidEvent {
    
    private final UUID invoiceId;
    private final UUID customerId;
    
    public InvoicePaidEvent(UUID invoiceId, UUID customerId) {
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


