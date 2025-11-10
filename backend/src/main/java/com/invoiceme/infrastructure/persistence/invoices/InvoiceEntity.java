package com.invoiceme.infrastructure.persistence.invoices;

import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceStatus;
import com.invoiceme.domain.invoices.LineItem;
import com.invoiceme.domain.payments.Payment;
import com.invoiceme.domain.payments.PaymentPlan;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA entity for Invoice.
 * Maps domain Invoice to database table.
 */
@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoices_customer_id", columnList = "customer_id"),
    @Index(name = "idx_invoices_status", columnList = "status"),
    @Index(name = "idx_invoices_customer_status", columnList = "customer_id,status")
})
class InvoiceEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_plan", nullable = false)
    private PaymentPlan paymentPlan;
    
    @Column(name = "discount_code", length = 50)
    private String discountCode;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private java.math.BigDecimal discountAmount;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LineItemEntity> lineItems = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Note: Payments are stored separately (separate aggregate)
    // They are loaded separately and added to domain entity
    
    // Default constructor for JPA
    protected InvoiceEntity() {
    }
    
    // Convert from domain entity
    static InvoiceEntity fromDomain(Invoice invoice) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.id = invoice.getId();
        entity.customerId = invoice.getCustomerId();
        entity.status = invoice.getStatus();
        entity.issueDate = invoice.getIssueDate();
        entity.dueDate = invoice.getDueDate();
        entity.paymentPlan = invoice.getPaymentPlan();
        entity.discountCode = invoice.getDiscountCode();
        entity.discountAmount = invoice.getDiscountAmount();
        entity.createdAt = invoice.getCreatedAt();
        entity.updatedAt = invoice.getUpdatedAt();
        
        // Convert line items with order
        List<LineItem> domainLineItems = invoice.getLineItems();
        entity.lineItems = new ArrayList<>();
        for (int i = 0; i < domainLineItems.size(); i++) {
            entity.lineItems.add(LineItemEntity.fromDomain(domainLineItems.get(i), entity, i));
        }
        
        return entity;
    }
    
    // Convert to domain entity (payments loaded separately)
    Invoice toDomain(List<Payment> payments) {
        List<LineItem> domainLineItems = lineItems.stream()
                .map(LineItemEntity::toDomain)
                .collect(Collectors.toList());
        
        return Invoice.reconstruct(
            id,
            customerId,
            status,
            issueDate,
            dueDate,
            paymentPlan,
            discountCode,
            discountAmount,
            domainLineItems,
            payments,
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
    
    UUID getCustomerId() {
        return customerId;
    }
    
    void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
    
    InvoiceStatus getStatus() {
        return status;
    }
    
    void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    
    LocalDate getIssueDate() {
        return issueDate;
    }
    
    void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    
    LocalDate getDueDate() {
        return dueDate;
    }
    
    void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    PaymentPlan getPaymentPlan() {
        return paymentPlan;
    }
    
    void setPaymentPlan(PaymentPlan paymentPlan) {
        this.paymentPlan = paymentPlan;
    }
    
    String getDiscountCode() {
        return discountCode;
    }
    
    void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
    
    java.math.BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    void setDiscountAmount(java.math.BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    List<LineItemEntity> getLineItems() {
        return lineItems;
    }
    
    void setLineItems(List<LineItemEntity> lineItems) {
        this.lineItems = lineItems;
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

