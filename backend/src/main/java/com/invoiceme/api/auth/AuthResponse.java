package com.invoiceme.api.auth;

/**
 * Response DTO for authentication operations.
 */
public record AuthResponse(
    String token,
    String username,
    String email
) {
}

