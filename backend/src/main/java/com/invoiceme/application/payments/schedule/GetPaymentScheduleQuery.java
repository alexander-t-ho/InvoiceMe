package com.invoiceme.application.payments.schedule;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Query to get payment schedule for an invoice.
 */
public record GetPaymentScheduleQuery(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId
) {
}









