package com.invoiceme.application.auth.unified;

import jakarta.validation.constraints.NotBlank;

/**
 * Command for unified login (supports both admin users and customers).
 */
public record UnifiedLoginCommand(
    @NotBlank(message = "Username or email is required")
    String identifier, // Can be username or email
    
    @NotBlank(message = "Password is required")
    String password
) {
}

