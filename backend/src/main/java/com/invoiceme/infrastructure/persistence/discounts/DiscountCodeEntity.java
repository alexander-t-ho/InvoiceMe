package com.invoiceme.infrastructure.persistence.discounts;

import com.invoiceme.domain.discounts.DiscountCode;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for DiscountCode.
 * Maps domain DiscountCode to database table.
 */
@Entity
@Table(name = "discount_codes", indexes = {
    @Index(name = "idx_discount_codes_code", columnList = "code", unique = true)
})
class DiscountCodeEntity {
    
    @Id
    @Column(length = 50)
    private String code;
    
    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor for JPA
    protected DiscountCodeEntity() {
    }
    
    // Convert from domain entity
    static DiscountCodeEntity fromDomain(DiscountCode discountCode) {
        DiscountCodeEntity entity = new DiscountCodeEntity();
        entity.code = discountCode.getCode();
        entity.discountPercent = discountCode.getDiscountPercent();
        entity.isActive = discountCode.isActive();
        entity.createdAt = discountCode.getCreatedAt();
        entity.updatedAt = discountCode.getUpdatedAt();
        return entity;
    }
    
    // Convert to domain entity
    DiscountCode toDomain() {
        return DiscountCode.reconstruct(
            code,
            discountPercent,
            isActive,
            createdAt,
            updatedAt
        );
    }
    
    // Getters and setters
    String getCode() {
        return code;
    }
    
    void setCode(String code) {
        this.code = code;
    }
    
    BigDecimal getDiscountPercent() {
        return discountPercent;
    }
    
    void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
    
    boolean isActive() {
        return isActive;
    }
    
    void setActive(boolean active) {
        isActive = active;
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









