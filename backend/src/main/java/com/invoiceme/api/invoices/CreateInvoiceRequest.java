package com.invoiceme.api.invoices;

import com.invoiceme.domain.payments.PaymentPlan;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating an invoice.
 */
public record CreateInvoiceRequest(
    @NotNull(message = "Customer ID is required")
    UUID customerId,
    
    @NotNull(message = "Issue date is required")
    LocalDate issueDate,
    
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    LocalDate dueDate,
    
    PaymentPlan paymentPlan
) {
}

