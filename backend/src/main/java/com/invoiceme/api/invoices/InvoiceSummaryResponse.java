package com.invoiceme.api.invoices;

import com.invoiceme.domain.invoices.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for invoice summary (used in list views).
 */
public record InvoiceSummaryResponse(
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

