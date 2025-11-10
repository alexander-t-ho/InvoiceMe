package com.invoiceme.application.items.update;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to update an existing item.
 */
public record UpdateItemCommand(
    @NotNull(message = "Item ID is required")
    UUID itemId,
    
    UUID userId,
    
    @NotBlank(message = "Description is required")
    String description,
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    BigDecimal unitPrice
) {
}



