package com.invoiceme.application.invoices.markAsSent;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to mark an invoice as SENT.
 */
public record MarkInvoiceAsSentCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId
) {
}


