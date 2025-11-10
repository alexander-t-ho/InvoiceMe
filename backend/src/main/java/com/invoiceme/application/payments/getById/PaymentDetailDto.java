package com.invoiceme.application.payments.getById;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Payment detail data.
 */
public record PaymentDetailDto(
    UUID id,
    UUID invoiceId,
    String invoiceNumber,
    BigDecimal amount,
    LocalDate paymentDate,
    String paymentMethod,
    LocalDateTime createdAt
) {
}

