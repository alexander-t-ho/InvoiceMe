package com.invoiceme.application.customers.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to update an existing customer.
 */
public record UpdateCustomerCommand(
    @NotNull(message = "Customer ID is required")
    UUID customerId,
    
    @NotBlank(message = "Customer name is required")
    String name,
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    String email,
    
    String address
) {
}


