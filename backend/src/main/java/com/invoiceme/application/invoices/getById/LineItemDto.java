package com.invoiceme.application.invoices.getById;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for LineItem data.
 */
public record LineItemDto(
    UUID id,
    String description,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal total
) {
}

