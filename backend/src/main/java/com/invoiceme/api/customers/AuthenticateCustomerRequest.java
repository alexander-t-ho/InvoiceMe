package com.invoiceme.api.customers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for authenticating a customer.
 */
public record AuthenticateCustomerRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {
}

