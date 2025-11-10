package com.invoiceme.application.customers.delete;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to delete a customer.
 */
public record DeleteCustomerCommand(
    @NotNull(message = "Customer ID is required")
    UUID customerId
) {
}


