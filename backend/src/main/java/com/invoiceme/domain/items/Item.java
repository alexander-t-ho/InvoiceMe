package com.invoiceme.domain.items;

import com.invoiceme.domain.exceptions.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Item domain entity.
 * Represents a reusable item in the item library.
 * 
 * Business Rules:
 * - Description is required and cannot be empty
 * - Unit price must be >= 0
 * - User ID is required
 */
public class Item {
    
    private UUID id;
    private UUID userId;
    private String description;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Private constructor for domain creation
    private Item() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Factory method to create a new Item.
     * Validates the input before creating the entity.
     */
    public static Item create(UUID userId, String description, BigDecimal unitPrice) {
        Item item = new Item();
        item.setUserId(userId);
        item.setDescription(description);
        item.setUnitPrice(unitPrice);
        item.validate();
        return item;
    }
    
    /**
     * Validates the item entity.
     * Throws DomainValidationException if validation fails.
     */
    public void validate() {
        if (userId == null) {
            throw new DomainValidationException("Item user ID cannot be null");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new DomainValidationException("Item description cannot be null or empty");
        }
        if (unitPrice == null) {
            throw new DomainValidationException("Item unit price cannot be null");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("Item unit price cannot be negative");
        }
    }
    
    /**
     * Updates the item's unit price.
     */
    public void updatePrice(BigDecimal newUnitPrice) {
        if (newUnitPrice == null) {
            throw new DomainValidationException("Unit price cannot be null");
        }
        if (newUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("Unit price cannot be negative");
        }
        this.unitPrice = newUnitPrice;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates the item's description.
     */
    public void updateDescription(String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new DomainValidationException("Description cannot be null or empty");
        }
        this.description = newDescription.trim();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates both description and unit price.
     */
    public void updateDetails(String description, BigDecimal unitPrice) {
        updateDescription(description);
        updatePrice(unitPrice);
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Factory method to reconstruct Item from persistence.
     * Used by repository implementations.
     */
    public static Item reconstruct(
            UUID id,
            UUID userId,
            String description,
            BigDecimal unitPrice,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        Item item = new Item();
        item.id = id;
        item.userId = userId;
        item.description = description;
        item.unitPrice = unitPrice;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;
        return item;
    }
    
    // Setters (package-private for domain operations)
    void setId(UUID id) {
        this.id = id;
    }
    
    void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }
    
    void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }
}









