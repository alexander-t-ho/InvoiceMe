package com.invoiceme.application.payments.schedule;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to create a payment schedule for an invoice.
 */
public record CreatePaymentScheduleCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    @NotNull(message = "Total amount is required")
    BigDecimal totalAmount,
    
    @NotNull(message = "Start date is required")
    java.time.LocalDate startDate
) {
}



