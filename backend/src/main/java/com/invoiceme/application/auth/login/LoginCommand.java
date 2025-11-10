package com.invoiceme.application.auth.login;

/**
 * Command to login a user.
 */
public record LoginCommand(
    String username,
    String password
) {
}

