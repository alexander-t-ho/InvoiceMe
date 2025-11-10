package com.invoiceme.api.customers;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for customer data.
 */
public record CustomerResponse(
    UUID id,
    String name,
    String email,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

