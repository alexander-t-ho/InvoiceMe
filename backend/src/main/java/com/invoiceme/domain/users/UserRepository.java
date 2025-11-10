package com.invoiceme.domain.users;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User aggregate.
 */
public interface UserRepository {
    /**
     * Saves a user.
     */
    void save(User user);

    /**
     * Finds a user by ID.
     */
    Optional<User> findById(UUID id);

    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a username exists.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email exists.
     */
    boolean existsByEmail(String email);
}

