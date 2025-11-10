package com.invoiceme.domain.events;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain event published when a payment is recorded.
 */
public class PaymentRecordedEvent {
    
    private final UUID paymentId;
    private final UUID invoiceId;
    private final BigDecimal amount;
    
    public PaymentRecordedEvent(UUID paymentId, UUID invoiceId, BigDecimal amount) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amount = amount;
    }
    
    public UUID getPaymentId() {
        return paymentId;
    }
    
    public UUID getInvoiceId() {
        return invoiceId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
}


