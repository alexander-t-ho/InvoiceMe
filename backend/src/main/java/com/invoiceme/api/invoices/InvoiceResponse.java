package com.invoiceme.api.invoices;

import com.invoiceme.domain.invoices.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for invoice data.
 */
public record InvoiceResponse(
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
    List<LineItemResponse> lineItems,
    List<PaymentResponse> payments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

