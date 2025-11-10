package com.invoiceme.domain.users;

import com.invoiceme.domain.exceptions.DomainValidationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User domain entity for authentication.
 */
public class User {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Private constructor for domain creation
    private User() {
    }

    /**
     * Creates a new user.
     */
    public static User create(String username, String email, String plainPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new DomainValidationException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new DomainValidationException("Email is required");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new DomainValidationException("Password must be at least 6 characters");
        }

        User user = new User();
        user.id = UUID.randomUUID();
        user.username = username.trim();
        user.email = email.trim().toLowerCase();
        user.passwordHash = passwordEncoder.encode(plainPassword);
        user.createdAt = LocalDateTime.now();
        return user;
    }

    /**
     * Validates a plain password against the stored hash.
     */
    public boolean validatePassword(String plainPassword) {
        if (plainPassword == null || this.passwordHash == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, this.passwordHash);
    }

    /**
     * Changes the user's password.
     */
    public void changePassword(String newPlainPassword) {
        if (newPlainPassword == null || newPlainPassword.length() < 6) {
            throw new DomainValidationException("Password must be at least 6 characters");
        }
        this.passwordHash = passwordEncoder.encode(newPlainPassword);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Reconstructs a User from persistence.
     */
    public static User reconstruct(
            UUID id,
            String username,
            String email,
            String passwordHash,
            LocalDateTime createdAt) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.email = email;
        user.passwordHash = passwordHash;
        user.createdAt = createdAt;
        return user;
    }
}

