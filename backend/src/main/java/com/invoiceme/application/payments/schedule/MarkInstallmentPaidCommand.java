package com.invoiceme.application.payments.schedule;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to mark an installment as paid.
 */
public record MarkInstallmentPaidCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    @NotNull(message = "Payment amount is required")
    java.math.BigDecimal paymentAmount
) {
}



