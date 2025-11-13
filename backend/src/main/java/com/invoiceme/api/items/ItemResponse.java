package com.invoiceme.api.items;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for item data.
 */
public record ItemResponse(
    UUID id,
    UUID userId,
    String description,
    BigDecimal unitPrice,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}









