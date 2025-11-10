package com.invoiceme.application.customers.getById;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Customer data returned by queries.
 */
public record CustomerDto(
    UUID id,
    String name,
    String email,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

