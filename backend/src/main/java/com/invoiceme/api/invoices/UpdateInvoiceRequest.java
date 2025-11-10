package com.invoiceme.api.invoices;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;

/**
 * Request DTO for updating an invoice.
 */
public record UpdateInvoiceRequest(
    @NotNull(message = "Issue date is required")
    LocalDate issueDate,
    
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    LocalDate dueDate
) {
}

