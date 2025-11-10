package com.invoiceme.api.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for unified login.
 */
public record UnifiedLoginRequest(
    @NotBlank(message = "Username or email is required")
    String identifier,
    
    @NotBlank(message = "Password is required")
    String password
) {
}

