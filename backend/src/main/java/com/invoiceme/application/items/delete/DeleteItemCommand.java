package com.invoiceme.application.items.delete;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to delete an item.
 */
public record DeleteItemCommand(
    @NotNull(message = "Item ID is required")
    UUID itemId,
    
    UUID userId
) {
}



