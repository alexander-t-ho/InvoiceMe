package com.invoiceme.application.customers.getByEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Query to get a customer by email.
 */
public record GetCustomerByEmailQuery(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email
) {
}

