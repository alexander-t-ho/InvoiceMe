package com.invoiceme.application.payments.record;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to record a payment for an invoice.
 */
public record RecordPaymentCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,
    
    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,
    
    String paymentMethod
) {
}


