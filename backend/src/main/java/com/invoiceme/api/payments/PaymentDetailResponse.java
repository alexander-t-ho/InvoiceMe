package com.invoiceme.api.payments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for payment detail data.
 */
public record PaymentDetailResponse(
    UUID id,
    UUID invoiceId,
    String invoiceNumber,
    BigDecimal amount,
    LocalDate paymentDate,
    String paymentMethod,
    LocalDateTime createdAt
) {
}

