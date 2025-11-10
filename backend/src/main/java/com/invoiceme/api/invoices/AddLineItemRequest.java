package com.invoiceme.api.invoices;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for adding a line item to an invoice.
 * If itemId is provided, description and unitPrice will be auto-filled from the item library.
 */
public record AddLineItemRequest(
    UUID itemId, // Optional: if provided, will auto-fill description and unitPrice
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    BigDecimal quantity,
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    BigDecimal unitPrice
) {
}

