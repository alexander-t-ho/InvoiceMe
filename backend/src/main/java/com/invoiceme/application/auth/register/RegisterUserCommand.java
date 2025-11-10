package com.invoiceme.application.auth.register;

/**
 * Command to register a new user.
 */
public record RegisterUserCommand(
    String username,
    String email,
    String password
) {
}

