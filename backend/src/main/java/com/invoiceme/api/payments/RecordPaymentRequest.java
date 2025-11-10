package com.invoiceme.api.payments;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for recording a payment.
 */
public record RecordPaymentRequest(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    BigDecimal amount,
    
    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,
    
    @NotBlank(message = "Payment method is required")
    String paymentMethod
) {
}

