package com.invoiceme.infrastructure.persistence.users;

import com.invoiceme.domain.users.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for User.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
}, indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_email", columnList = "email")
})
public class UserEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public UserEntity() {
    }

    // Constructor from domain entity
    public UserEntity(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.createdAt = user.getCreatedAt();
    }

    // Convert to domain entity
    public User toDomain() {
        return User.reconstruct(
            id,
            username,
            email,
            passwordHash,
            createdAt
        );
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

