package com.invoiceme.application.discounts.validate;

import jakarta.validation.constraints.NotBlank;

/**
 * Query to validate a discount code.
 */
public record ValidateDiscountCodeQuery(
    @NotBlank(message = "Discount code is required")
    String code
) {
}



