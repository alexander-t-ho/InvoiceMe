package com.invoiceme.domain.payments;

import com.invoiceme.domain.exceptions.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * PaymentSchedule domain entity.
 * Represents a scheduled payment installment for Pay in 4.
 * 
 * Business Rules:
 * - Installment number must be 1-4
 * - Amount must be positive
 * - Due date must be in the future (when created)
 * - Status transitions: PENDING → PAID (no backward transitions)
 */
public class PaymentSchedule {
    
    public enum InstallmentStatus {
        PENDING,
        PAID,
        OVERDUE
    }
    
    private UUID id;
    private UUID invoiceId;
    private int installmentNumber; // 1-4
    private BigDecimal amount;
    private LocalDate dueDate;
    private InstallmentStatus status;
    private LocalDateTime createdAt;
    
    // Private constructor for domain creation
    private PaymentSchedule() {
        this.status = InstallmentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Factory method to create a new PaymentSchedule.
     * Validates the input before creating the entity.
     */
    public static PaymentSchedule create(
            UUID invoiceId,
            int installmentNumber,
            BigDecimal amount,
            LocalDate dueDate) {
        PaymentSchedule schedule = new PaymentSchedule();
        schedule.id = UUID.randomUUID();
        schedule.invoiceId = invoiceId;
        schedule.setInstallmentNumber(installmentNumber);
        schedule.setAmount(amount);
        schedule.setDueDate(dueDate);
        schedule.validate();
        return schedule;
    }
    
    /**
     * Validates the payment schedule entity.
     * Throws DomainValidationException if validation fails.
     */
    public void validate() {
        if (invoiceId == null) {
            throw new DomainValidationException("Invoice ID cannot be null");
        }
        if (installmentNumber < 1 || installmentNumber > 4) {
            throw new DomainValidationException("Installment number must be between 1 and 4");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("Amount must be greater than zero");
        }
        if (dueDate == null) {
            throw new DomainValidationException("Due date cannot be null");
        }
    }
    
    /**
     * Marks the installment as paid.
     */
    public void markAsPaid() {
        if (status == InstallmentStatus.PAID) {
            throw new DomainValidationException("Installment is already paid");
        }
        this.status = InstallmentStatus.PAID;
    }
    
    /**
     * Marks the installment as overdue.
     */
    public void markAsOverdue() {
        if (status == InstallmentStatus.PAID) {
            return; // Don't mark paid installments as overdue
        }
        if (LocalDate.now().isAfter(dueDate)) {
            this.status = InstallmentStatus.OVERDUE;
        }
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public UUID getInvoiceId() {
        return invoiceId;
    }
    
    public int getInstallmentNumber() {
        return installmentNumber;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public InstallmentStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Factory method to reconstruct PaymentSchedule from persistence.
     * Used by repository implementations.
     */
    public static PaymentSchedule reconstruct(
            UUID id,
            UUID invoiceId,
            int installmentNumber,
            BigDecimal amount,
            LocalDate dueDate,
            InstallmentStatus status,
            LocalDateTime createdAt) {
        PaymentSchedule schedule = new PaymentSchedule();
        schedule.id = id;
        schedule.invoiceId = invoiceId;
        schedule.installmentNumber = installmentNumber;
        schedule.amount = amount;
        schedule.dueDate = dueDate;
        schedule.status = status;
        schedule.createdAt = createdAt;
        return schedule;
    }
    
    // Setters (package-private for domain operations)
    void setId(UUID id) {
        this.id = id;
    }
    
    void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }
    
    void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    void setStatus(InstallmentStatus status) {
        this.status = status;
    }
    
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentSchedule that = (PaymentSchedule) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "PaymentSchedule{" +
                "id=" + id +
                ", invoiceId=" + invoiceId +
                ", installmentNumber=" + installmentNumber +
                ", amount=" + amount +
                ", dueDate=" + dueDate +
                ", status=" + status +
                '}';
    }
}









