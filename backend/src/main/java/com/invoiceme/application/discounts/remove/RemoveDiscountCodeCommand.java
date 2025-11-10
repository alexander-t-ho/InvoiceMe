package com.invoiceme.application.discounts.remove;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to remove a discount code from an invoice.
 */
public record RemoveDiscountCodeCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId
) {
}



