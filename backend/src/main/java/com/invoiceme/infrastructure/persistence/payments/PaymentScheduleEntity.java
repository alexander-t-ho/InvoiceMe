package com.invoiceme.infrastructure.persistence.payments;

import com.invoiceme.domain.payments.PaymentSchedule;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for PaymentSchedule.
 * Maps domain PaymentSchedule to database table.
 */
@Entity
@Table(name = "payment_schedules", indexes = {
    @Index(name = "idx_payment_schedules_invoice_id", columnList = "invoice_id"),
    @Index(name = "idx_payment_schedules_due_date", columnList = "due_date")
})
class PaymentScheduleEntity {
    
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "invoice_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID invoiceId;
    
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentSchedule.InstallmentStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Default constructor for JPA
    protected PaymentScheduleEntity() {
    }
    
    // Convert from domain entity
    static PaymentScheduleEntity fromDomain(PaymentSchedule schedule) {
        PaymentScheduleEntity entity = new PaymentScheduleEntity();
        entity.id = schedule.getId();
        entity.invoiceId = schedule.getInvoiceId();
        entity.installmentNumber = schedule.getInstallmentNumber();
        entity.amount = schedule.getAmount();
        entity.dueDate = schedule.getDueDate();
        entity.status = schedule.getStatus();
        entity.createdAt = schedule.getCreatedAt();
        return entity;
    }
    
    // Convert to domain entity
    PaymentSchedule toDomain() {
        return PaymentSchedule.reconstruct(
            id,
            invoiceId,
            installmentNumber,
            amount,
            dueDate,
            status,
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
    
    Integer getInstallmentNumber() {
        return installmentNumber;
    }
    
    void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }
    
    BigDecimal getAmount() {
        return amount;
    }
    
    void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    LocalDate getDueDate() {
        return dueDate;
    }
    
    void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    PaymentSchedule.InstallmentStatus getStatus() {
        return status;
    }
    
    void setStatus(PaymentSchedule.InstallmentStatus status) {
        this.status = status;
    }
    
    LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}



