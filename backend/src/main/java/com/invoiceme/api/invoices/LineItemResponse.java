package com.invoiceme.api.invoices;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for line item data.
 */
public record LineItemResponse(
    UUID id,
    String description,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal total
) {
}

