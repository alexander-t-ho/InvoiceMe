package com.invoiceme.application.customers.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Command to create a new customer.
 */
public record CreateCustomerCommand(
    @NotBlank(message = "Customer name is required")
    String name,
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    String email,
    
    String address,
    
    String password // Optional, defaults to "123456" if not provided
) {
}


