package com.invoiceme.application.invoices.listByStatus;

import com.invoiceme.domain.invoices.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Query to list invoices filtered by status with pagination.
 */
public record ListInvoicesByStatusQuery(
    @NotNull(message = "Status is required")
    InvoiceStatus status,
    int page,
    int size
) {
    public ListInvoicesByStatusQuery {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100; // Max page size
    }
}

