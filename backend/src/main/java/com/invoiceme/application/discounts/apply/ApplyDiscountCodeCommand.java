package com.invoiceme.application.discounts.apply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to apply a discount code to an invoice.
 */
public record ApplyDiscountCodeCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    @NotBlank(message = "Discount code is required")
    String discountCode
) {
}









