package com.invoiceme.infrastructure.persistence.invoices;

import com.invoiceme.domain.invoices.LineItem;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA entity for LineItem.
 * Maps domain LineItem value object to database table.
 */
@Entity
@Table(name = "invoice_line_items", indexes = {
    @Index(name = "idx_invoice_line_items_invoice_id", columnList = "invoice_id")
})
class LineItemEntity {
    
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "line_order", nullable = false)
    private Integer lineOrder;
    
    // Default constructor for JPA
    protected LineItemEntity() {
    }
    
    // Convert from domain value object
    static LineItemEntity fromDomain(LineItem lineItem, InvoiceEntity invoice) {
        return fromDomain(lineItem, invoice, 0);
    }
    
    // Convert from domain with line order
    static LineItemEntity fromDomain(LineItem lineItem, InvoiceEntity invoice, int lineOrder) {
        LineItemEntity entity = new LineItemEntity();
        entity.id = lineItem.getId();
        entity.invoice = invoice;
        entity.description = lineItem.getDescription();
        entity.quantity = lineItem.getQuantity();
        entity.unitPrice = lineItem.getUnitPrice();
        entity.total = lineItem.getTotal();
        entity.lineOrder = lineOrder;
        return entity;
    }
    
    // Convert to domain value object
    LineItem toDomain() {
        return LineItem.of(id, description, quantity, unitPrice);
    }
    
    // Getters and setters
    UUID getId() {
        return id;
    }
    
    void setId(UUID id) {
        this.id = id;
    }
    
    InvoiceEntity getInvoice() {
        return invoice;
    }
    
    void setInvoice(InvoiceEntity invoice) {
        this.invoice = invoice;
    }
    
    String getDescription() {
        return description;
    }
    
    void setDescription(String description) {
        this.description = description;
    }
    
    BigDecimal getQuantity() {
        return quantity;
    }
    
    void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    BigDecimal getTotal() {
        return total;
    }
    
    void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    Integer getLineOrder() {
        return lineOrder;
    }
    
    void setLineOrder(Integer lineOrder) {
        this.lineOrder = lineOrder;
    }
}

