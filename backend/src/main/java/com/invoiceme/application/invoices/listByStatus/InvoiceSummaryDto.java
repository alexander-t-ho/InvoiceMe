package com.invoiceme.application.invoices.listByStatus;

import com.invoiceme.domain.invoices.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Lightweight DTO for invoice summary (used in list queries).
 */
public record InvoiceSummaryDto(
    UUID id,
    UUID customerId,
    String customerName,
    InvoiceStatus status,
    LocalDate issueDate,
    LocalDate dueDate,
    BigDecimal totalAmount,
    BigDecimal balance
) {
}

