package com.invoiceme.application.invoices.getById;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Payment data.
 */
public record PaymentDto(
    UUID id,
    BigDecimal amount,
    LocalDate paymentDate,
    String paymentMethod,
    LocalDateTime createdAt
) {
}

