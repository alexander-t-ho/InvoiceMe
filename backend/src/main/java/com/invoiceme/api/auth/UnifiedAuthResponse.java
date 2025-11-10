package com.invoiceme.api.auth;

/**
 * Response DTO for unified authentication.
 */
public record UnifiedAuthResponse(
    String userType, // "ADMIN" or "CUSTOMER"
    String token,
    String userId,
    String name,
    String email,
    String customerId
) {
}

