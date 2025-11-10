package com.invoiceme.application.invoices.listByCustomer;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Query to list invoices filtered by customer with pagination.
 */
public record ListInvoicesByCustomerQuery(
    @NotNull(message = "Customer ID is required")
    UUID customerId,
    int page,
    int size
) {
    public ListInvoicesByCustomerQuery {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100; // Max page size
    }
}

