package com.invoiceme.infrastructure.persistence.payments;

import com.invoiceme.domain.payments.Payment;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for Payment.
 * Maps domain Payment to database table.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_invoice_id", columnList = "invoice_id")
})
public class PaymentEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Default constructor for JPA
    protected PaymentEntity() {
    }
    
    // Convert from domain entity
    static PaymentEntity fromDomain(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.id = payment.getId();
        entity.invoiceId = payment.getInvoiceId();
        entity.amount = payment.getAmount();
        entity.paymentDate = payment.getPaymentDate();
        entity.paymentMethod = payment.getPaymentMethod();
        entity.createdAt = payment.getCreatedAt();
        return entity;
    }
    
    // Convert to domain entity
    public Payment toDomain() {
        return Payment.reconstruct(
            id,
            invoiceId,
            amount,
            paymentDate,
            paymentMethod,
            createdAt
        );
    }
    
    // Getters and setters
    UUID getId() {
        return id;
    }
    
    void setId(UUID id) {
        this.id = id;
    }
    
    UUID getInvoiceId() {
        return invoiceId;
    }
    
    void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    BigDecimal getAmount() {
        return amount;
    }
    
    void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    String getPaymentMethod() {
        return paymentMethod;
    }
    
    void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

