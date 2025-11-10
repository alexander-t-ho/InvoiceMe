package com.invoiceme.api.customers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a customer.
 */
public record CreateCustomerRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    String address,
    
    String password // Optional, defaults to "123456" if not provided
) {
}

