package com.invoiceme.domain.customers;

import com.invoiceme.domain.exceptions.DomainValidationException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Customer domain entity.
 * Represents a customer in the invoicing system.
 * 
 * Business Rules:
 * - Email must be unique across all customers
 * - Email must be in valid format
 * - Name cannot be null or empty
 */
public class Customer {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private UUID id;
    private String name;
    private String email;
    private String address;
    private String passwordHash; // BCrypt hashed password
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Private constructor for domain creation
    private Customer() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Factory method to create a new Customer.
     * Validates the input before creating the entity.
     */
    public static Customer create(String name, String email, String address, String passwordHash) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setPasswordHash(passwordHash);
        customer.validate();
        return customer;
    }
    
    /**
     * Validates the customer entity.
     * Throws DomainValidationException if validation fails.
     */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidationException("Customer name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new DomainValidationException("Customer email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new DomainValidationException("Customer email must be in valid format");
        }
    }
    
    /**
     * Updates customer details.
     * Validates the new values before updating.
     */
    public void updateDetails(String name, String email, String address) {
        this.setName(name);
        this.setEmail(email);
        this.setAddress(address);
        this.validate();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates customer password.
     */
    public void updatePassword(String passwordHash) {
        this.setPasswordHash(passwordHash);
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Factory method to reconstruct Customer from persistence.
     * Used by repository implementations.
     */
    public static Customer reconstruct(
            UUID id,
            String name,
            String email,
            String address,
            String passwordHash,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        Customer customer = new Customer();
        customer.id = id;
        customer.name = name;
        customer.email = email;
        customer.address = address;
        customer.passwordHash = passwordHash;
        customer.createdAt = createdAt;
        customer.updatedAt = updatedAt;
        return customer;
    }
    
    // Setters (package-private for domain operations)
    void setId(UUID id) {
        this.id = id;
    }
    
    void setName(String name) {
        this.name = name;
    }
    
    void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }
    
    void setAddress(String address) {
        this.address = address;
    }
    
    void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

