package com.invoiceme.application.payments.listByInvoice;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Query to list all payments for an invoice.
 */
public record ListPaymentsByInvoiceQuery(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId
) {
}

