package com.invoiceme.infrastructure.persistence.items;

import com.invoiceme.domain.items.Item;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for Item.
 * Maps domain Item to database table.
 */
@Entity
@Table(name = "items", indexes = {
    @Index(name = "idx_items_user_id", columnList = "user_id"),
    @Index(name = "idx_items_description", columnList = "description")
})
class ItemEntity {
    
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor for JPA
    protected ItemEntity() {
    }
    
    // Convert from domain entity
    static ItemEntity fromDomain(Item item) {
        ItemEntity entity = new ItemEntity();
        entity.id = item.getId();
        entity.userId = item.getUserId();
        entity.description = item.getDescription();
        entity.unitPrice = item.getUnitPrice();
        entity.createdAt = item.getCreatedAt();
        entity.updatedAt = item.getUpdatedAt();
        return entity;
    }
    
    // Convert to domain entity
    Item toDomain() {
        return Item.reconstruct(
            id,
            userId,
            description,
            unitPrice,
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
    
    UUID getUserId() {
        return userId;
    }
    
    void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    String getDescription() {
        return description;
    }
    
    void setDescription(String description) {
        this.description = description;
    }
    
    BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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





