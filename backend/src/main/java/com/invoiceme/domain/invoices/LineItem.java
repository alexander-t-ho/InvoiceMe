package com.invoiceme.domain.invoices;

import com.invoiceme.domain.exceptions.InvalidLineItemException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * LineItem value object.
 * Represents a single line item on an invoice.
 * Immutable value object.
 */
public class LineItem {
    
    private final UUID id;
    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal total;
    
    private LineItem(UUID id, String description, BigDecimal quantity, BigDecimal unitPrice) {
        if (id == null) {
            throw new InvalidLineItemException("Line item ID cannot be null");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidLineItemException("Line item description cannot be null or empty");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidLineItemException("Line item quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidLineItemException("Line item unit price cannot be negative");
        }
        
        this.id = id;
        this.description = description.trim();
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = quantity.multiply(unitPrice);
    }
    
    /**
     * Factory method to create a new LineItem.
     */
    public static LineItem create(String description, BigDecimal quantity, BigDecimal unitPrice) {
        return new LineItem(UUID.randomUUID(), description, quantity, unitPrice);
    }
    
    /**
     * Factory method to create a LineItem with a specific ID (for reconstruction from persistence).
     */
    public static LineItem of(UUID id, String description, BigDecimal quantity, BigDecimal unitPrice) {
        return new LineItem(id, description, quantity, unitPrice);
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineItem lineItem = (LineItem) o;
        return Objects.equals(id, lineItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "LineItem{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", total=" + total +
                '}';
    }
}

