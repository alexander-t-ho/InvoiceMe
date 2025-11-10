package com.invoiceme.domain.payments;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Payment domain entity.
 * Represents a payment applied to an invoice.
 * 
 * Business Rules:
 * - Payment amount must be positive
 * - Payment date cannot be in the future
 * - Payment cannot exceed invoice remaining balance
 */
public class Payment {
    
    private UUID id;
    private UUID invoiceId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private LocalDateTime createdAt;
    
    // Private constructor for domain creation
    private Payment() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Factory method to create a new Payment.
     * Validates the input before creating the entity.
     */
    public static Payment create(UUID invoiceId, BigDecimal amount, LocalDate paymentDate, String paymentMethod) {
        Payment payment = new Payment();
        payment.invoiceId = invoiceId;
        payment.amount = amount;
        payment.paymentDate = paymentDate;
        payment.paymentMethod = paymentMethod;
        payment.validate();
        return payment;
    }
    
    /**
     * Validates the payment entity.
     * Throws DomainValidationException if validation fails.
     */
    public void validate() {
        if (invoiceId == null) {
            throw new DomainValidationException("Payment invoice ID cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("Payment amount must be greater than zero");
        }
        if (paymentDate == null) {
            throw new DomainValidationException("Payment date cannot be null");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new DomainValidationException("Payment date cannot be in the future");
        }
    }
    
    /**
     * Validates the payment against an invoice.
     * Checks that payment doesn't exceed invoice balance.
     */
    public void validateAgainstInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        if (!invoice.getId().equals(this.invoiceId)) {
            throw new IllegalArgumentException("Payment invoice ID does not match provided invoice");
        }
        
        BigDecimal invoiceBalance = invoice.calculateBalance();
        if (amount.compareTo(invoiceBalance) > 0) {
            throw new com.invoiceme.domain.exceptions.InsufficientPaymentException(
                String.format(
                    "Payment amount %.2f exceeds invoice balance %.2f",
                    amount,
                    invoiceBalance
                )
            );
        }
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public UUID getInvoiceId() {
        return invoiceId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Factory method to reconstruct Payment from persistence.
     * Used by repository implementations.
     */
    public static Payment reconstruct(
            UUID id,
            UUID invoiceId,
            BigDecimal amount,
            LocalDate paymentDate,
            String paymentMethod,
            LocalDateTime createdAt) {
        Payment payment = new Payment();
        payment.id = id;
        payment.invoiceId = invoiceId;
        payment.amount = amount;
        payment.paymentDate = paymentDate;
        payment.paymentMethod = paymentMethod;
        payment.createdAt = createdAt;
        return payment;
    }
    
    // Setters (package-private for domain operations and persistence)
    void setId(UUID id) {
        this.id = id;
    }
    
    void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", invoiceId=" + invoiceId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                '}';
    }
}

