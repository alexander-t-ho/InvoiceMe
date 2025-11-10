package com.invoiceme.application.customers.authenticate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Command to authenticate a customer by email and password.
 */
public record AuthenticateCustomerCommand(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {
}

