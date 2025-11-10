package com.invoiceme.application.invoices.addLineItem;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to add a line item to an invoice.
 * If itemId is provided, description and unitPrice will be auto-filled from the item library.
 */
public record AddLineItemCommand(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    UUID itemId, // Optional: if provided, will auto-fill description and unitPrice
    
    @NotBlank(message = "Description is required")
    String description,
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    BigDecimal quantity,
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    BigDecimal unitPrice
) {
}


