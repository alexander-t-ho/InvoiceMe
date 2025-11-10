package com.invoiceme.application.invoices.create;

import com.invoiceme.domain.payments.PaymentPlan;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to create a new invoice in DRAFT status.
 */
public record CreateInvoiceCommand(
    @NotNull(message = "Customer ID is required")
    UUID customerId,
    
    @NotNull(message = "Issue date is required")
    LocalDate issueDate,
    
    LocalDate dueDate,
    
    PaymentPlan paymentPlan
) {
}


