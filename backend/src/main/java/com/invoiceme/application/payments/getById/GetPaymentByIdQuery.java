package com.invoiceme.application.payments.getById;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Query to get a payment by ID.
 */
public record GetPaymentByIdQuery(
    @NotNull(message = "Payment ID is required")
    UUID paymentId
) {
}

