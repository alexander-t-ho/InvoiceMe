package com.invoiceme.infrastructure.persistence.customers;

import com.invoiceme.domain.customers.Customer;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for Customer.
 * Maps domain Customer to database table.
 */
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customers_email", columnList = "email")
})
class CustomerEntity {
    
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(nullable = true) // Temporarily nullable for existing customers, will be set to NOT NULL after migration
    private String passwordHash; // BCrypt hashed password
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor for JPA
    protected CustomerEntity() {
    }
    
    // Convert from domain entity
    static CustomerEntity fromDomain(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.id = customer.getId();
        entity.name = customer.getName();
        entity.email = customer.getEmail();
        entity.address = customer.getAddress();
        entity.passwordHash = customer.getPasswordHash();
        entity.createdAt = customer.getCreatedAt();
        entity.updatedAt = customer.getUpdatedAt();
        return entity;
    }
    
    // Convert to domain entity
    Customer toDomain() {
        return Customer.reconstruct(
            id,
            name,
            email,
            address,
            passwordHash != null ? passwordHash : "", // Default to empty string for existing customers
            createdAt,
            updatedAt
        );
    }
    
    // Getters and setters
    UUID getId() {
        return id;
    }
    
    void setId(UUID id) {
        this.id = id;
    }
    
    String getName() {
        return name;
    }
    
    void setName(String name) {
        this.name = name;
    }
    
    String getEmail() {
        return email;
    }
    
    void setEmail(String email) {
        this.email = email;
    }
    
    String getAddress() {
        return address;
    }
    
    void setAddress(String address) {
        this.address = address;
    }
    
    String getPasswordHash() {
        return passwordHash;
    }
    
    void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

