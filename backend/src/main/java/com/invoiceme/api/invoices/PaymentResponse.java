package com.invoiceme.api.invoices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for payment data (used in invoice context).
 */
public record PaymentResponse(
    UUID id,
    BigDecimal amount,
    LocalDate paymentDate,
    String paymentMethod,
    LocalDateTime createdAt
) {
}

