package com.invoiceme.application.invoices.getById;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Query to get an invoice by ID.
 */
public record GetInvoiceByIdQuery(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId
) {
}

