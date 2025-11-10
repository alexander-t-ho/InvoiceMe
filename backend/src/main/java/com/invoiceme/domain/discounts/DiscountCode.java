package com.invoiceme.domain.discounts;

import com.invoiceme.domain.exceptions.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DiscountCode domain entity.
 * Represents a discount code that can be applied to invoices.
 * 
 * Business Rules:
 * - Code must be unique and uppercase
 * - Discount percent must be between 0 and 100
 * - Code cannot be null or empty
 */
public class DiscountCode {
    
    private String code;
    private BigDecimal discountPercent;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Private constructor for domain creation
    private DiscountCode() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    /**
     * Factory method to create a new DiscountCode.
     * Validates the input before creating the entity.
     */
    public static DiscountCode create(String code, BigDecimal discountPercent) {
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(code);
        discountCode.setDiscountPercent(discountPercent);
        discountCode.validate();
        return discountCode;
    }
    
    /**
     * Validates the discount code entity.
     * Throws DomainValidationException if validation fails.
     */
    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new DomainValidationException("Discount code cannot be null or empty");
        }
        if (discountPercent == null) {
            throw new DomainValidationException("Discount percent cannot be null");
        }
        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
            throw new DomainValidationException("Discount percent must be between 0 and 100");
        }
    }
    
    /**
     * Activates the discount code.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Deactivates the discount code.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculates the discount amount for a given total.
     */
    public BigDecimal calculateDiscountAmount(BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return total.multiply(discountPercent).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }
    
    // Getters
    public String getCode() {
        return code;
    }
    
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Factory method to reconstruct DiscountCode from persistence.
     * Used by repository implementations.
     */
    public static DiscountCode reconstruct(
            String code,
            BigDecimal discountPercent,
            boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        DiscountCode discountCode = new DiscountCode();
        discountCode.code = code;
        discountCode.discountPercent = discountPercent;
        discountCode.isActive = isActive;
        discountCode.createdAt = createdAt;
        discountCode.updatedAt = updatedAt;
        return discountCode;
    }
    
    // Setters (package-private for domain operations)
    void setCode(String code) {
        this.code = code != null ? code.trim().toUpperCase() : null;
    }
    
    void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
    
    void setActive(boolean active) {
        isActive = active;
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
        DiscountCode that = (DiscountCode) o;
        return Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return "DiscountCode{" +
                "code='" + code + '\'' +
                ", discountPercent=" + discountPercent +
                ", isActive=" + isActive +
                '}';
    }
}



