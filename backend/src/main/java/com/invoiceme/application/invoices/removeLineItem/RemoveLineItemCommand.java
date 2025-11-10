package com.invoiceme.application.invoices.removeLineItem;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to remove a line item from an invoice.
 */
public record RemoveLineItemCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    @NotNull(message = "Line item ID is required")
    UUID lineItemId
) {
}


