package com.invoiceme.application.invoices.getById;

import com.invoiceme.domain.invoices.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Invoice data returned by queries.
 */
public record InvoiceDto(
    UUID id,
    UUID customerId,
    String customerName,
    InvoiceStatus status,
    LocalDate issueDate,
    LocalDate dueDate,
    com.invoiceme.domain.payments.PaymentPlan paymentPlan,
    String discountCode,
    BigDecimal discountAmount,
    BigDecimal subtotal,
    BigDecimal totalAmount,
    BigDecimal balance,
    List<LineItemDto> lineItems,
    List<PaymentDto> payments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

