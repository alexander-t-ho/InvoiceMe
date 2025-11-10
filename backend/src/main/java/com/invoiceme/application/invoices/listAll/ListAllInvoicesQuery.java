package com.invoiceme.application.invoices.listAll;

/**
 * Query to list all invoices regardless of status.
 */
public record ListAllInvoicesQuery(
    int page,
    int size
) {
}

