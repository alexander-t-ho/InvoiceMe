package com.invoiceme.application.invoices.update;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to update an invoice (only allowed for DRAFT invoices).
 */
public record UpdateInvoiceCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    @NotNull(message = "Issue date is required")
    LocalDate issueDate,
    
    LocalDate dueDate
) {
}


