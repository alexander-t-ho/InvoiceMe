package com.invoiceme.application.customers.getById;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Query to get a customer by ID.
 */
public record GetCustomerByIdQuery(
    @NotNull(message = "Customer ID is required")
    UUID customerId
) {
}

