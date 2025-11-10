package com.invoiceme.domain.invoices;

import com.invoiceme.domain.exceptions.InsufficientPaymentException;
import com.invoiceme.domain.exceptions.InvalidInvoiceStateException;
import com.invoiceme.domain.exceptions.InvalidLineItemException;
import com.invoiceme.domain.payments.Payment;
import com.invoiceme.domain.payments.PaymentPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Invoice domain entity.
 * Represents an invoice in the invoicing system.
 * 
 * Business Rules:
 * - Invoice must have at least one line item before being sent
 * - Invoice cannot be modified after being sent (only payments can be applied)
 * - Balance cannot be negative
 * - Payment amount cannot exceed remaining balance
 * - Status transitions: DRAFT → SENT → PAID (no backward transitions)
 */
public class Invoice {
    
    private UUID id;
    private UUID customerId;
    private InvoiceStatus status;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private PaymentPlan paymentPlan; // Payment plan (FULL or PAY_IN_4)
    private String discountCode; // Nullable discount code
    private BigDecimal discountAmount; // Calculated discount amount
    private final List<LineItem> lineItems;
    private final List<Payment> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Private constructor for domain creation
    private Invoice() {
        this.id = UUID.randomUUID();
        this.status = InvoiceStatus.DRAFT;
        this.paymentPlan = PaymentPlan.FULL; // Default to full payment
        this.discountAmount = BigDecimal.ZERO;
        this.lineItems = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Factory method to create a new Invoice in DRAFT status.
     */
    public static Invoice create(UUID customerId, LocalDate issueDate, LocalDate dueDate, PaymentPlan paymentPlan) {
        Invoice invoice = new Invoice();
        invoice.customerId = customerId;
        invoice.issueDate = issueDate;
        invoice.dueDate = dueDate;
        invoice.paymentPlan = paymentPlan != null ? paymentPlan : PaymentPlan.FULL;
        return invoice;
    }
    
    /**
     * Adds a line item to the invoice.
     * Only allowed if invoice is in DRAFT status.
     */
    public void addLineItem(LineItem item) {
        if (item == null) {
            throw new InvalidLineItemException("Line item cannot be null");
        }
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                "Cannot add line items to invoice in status: " + status
            );
        }
        lineItems.add(item);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Removes a line item from the invoice.
     * Only allowed if invoice is in DRAFT status.
     */
    public void removeLineItem(UUID lineItemId) {
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                "Cannot remove line items from invoice in status: " + status
            );
        }
        boolean removed = lineItems.removeIf(item -> item.getId().equals(lineItemId));
        if (!removed) {
            throw new InvalidLineItemException("Line item with ID " + lineItemId + " not found");
        }
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculates the subtotal amount from all line items (before discount).
     */
    public BigDecimal calculateSubtotal() {
        return lineItems.stream()
                .map(LineItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calculates the total amount after discount.
     * Total = subtotal - discount amount.
     */
    public BigDecimal calculateTotal() {
        BigDecimal subtotal = calculateSubtotal();
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount);
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total;
    }
    
    /**
     * Applies a discount code to the invoice.
     * Only allowed if invoice is in DRAFT status.
     * @param discountCode The discount code
     * @param discountPercent The discount percentage (0-100)
     */
    public void applyDiscount(String discountCode, BigDecimal discountPercent) {
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                "Cannot apply discount to invoice in status: " + status
            );
        }
        if (discountCode == null || discountCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount code cannot be null or empty");
        }
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0 
                || discountPercent.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100");
        }
        
        this.discountCode = discountCode.trim().toUpperCase();
        BigDecimal subtotal = calculateSubtotal();
        this.discountAmount = subtotal.multiply(discountPercent)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Removes the discount from the invoice.
     * Only allowed if invoice is in DRAFT status.
     */
    public void removeDiscount() {
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                "Cannot remove discount from invoice in status: " + status
            );
        }
        this.discountCode = null;
        this.discountAmount = BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the invoice as SENT.
     * Validates that invoice has at least one line item.
     */
    public void markAsSent() {
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                "Can only mark DRAFT invoices as SENT. Current status: " + status
            );
        }
        if (lineItems.isEmpty()) {
            throw new InvalidInvoiceStateException(
                "Cannot mark invoice as SENT without line items"
            );
        }
        this.status = InvoiceStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Applies a payment to the invoice.
     * Updates balance and transitions to PAID if balance reaches zero.
     */
    public void applyPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        if (status == InvoiceStatus.PAID) {
            throw new InvalidInvoiceStateException("Cannot apply payment to PAID invoice");
        }
        
        BigDecimal currentBalance = calculateBalance();
        if (payment.getAmount().compareTo(currentBalance) > 0) {
            throw new InsufficientPaymentException(
                String.format(
                    "Payment amount %.2f exceeds invoice balance %.2f",
                    payment.getAmount(),
                    currentBalance
                )
            );
        }
        
        payments.add(payment);
        
        // Transition to PAID if balance is zero
        if (calculateBalance().compareTo(BigDecimal.ZERO) == 0) {
            this.status = InvoiceStatus.PAID;
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculates the remaining balance.
     * Balance = total amount - sum of all payments.
     */
    public BigDecimal calculateBalance() {
        BigDecimal total = calculateTotal();
        BigDecimal paidAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.subtract(paidAmount);
    }
    
    /**
     * Checks if the invoice can be edited.
     * Only DRAFT invoices can be edited.
     */
    public boolean canBeEdited() {
        return status == InvoiceStatus.DRAFT;
    }
    
    /**
     * Checks if the invoice can be sent.
     * Only DRAFT invoices with at least one line item can be sent.
     */
    public boolean canBeSent() {
        return status == InvoiceStatus.DRAFT && !lineItems.isEmpty();
    }
    
    /**
     * Updates invoice dates.
     * Only allowed if invoice is in DRAFT status.
     */
    public void updateDates(LocalDate issueDate, LocalDate dueDate) {
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                "Cannot update invoice dates in status: " + status
            );
        }
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public InvoiceStatus getStatus() {
        return status;
    }
    
    public LocalDate getIssueDate() {
        return issueDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public PaymentPlan getPaymentPlan() {
        return paymentPlan;
    }
    
    public String getDiscountCode() {
        return discountCode;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount != null ? discountAmount : BigDecimal.ZERO;
    }
    
    public List<LineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }
    
    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Factory method to reconstruct Invoice from persistence.
     * Used by repository implementations.
     */
    public static Invoice reconstruct(
            UUID id,
            UUID customerId,
            InvoiceStatus status,
            LocalDate issueDate,
            LocalDate dueDate,
            PaymentPlan paymentPlan,
            String discountCode,
            BigDecimal discountAmount,
            List<LineItem> lineItems,
            List<Payment> payments,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        Invoice invoice = new Invoice();
        invoice.id = id;
        invoice.customerId = customerId;
        invoice.status = status;
        invoice.issueDate = issueDate;
        invoice.dueDate = dueDate;
        invoice.paymentPlan = paymentPlan != null ? paymentPlan : PaymentPlan.FULL;
        invoice.discountCode = discountCode;
        invoice.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        invoice.lineItems.clear();
        invoice.lineItems.addAll(lineItems);
        invoice.payments.clear();
        invoice.payments.addAll(payments);
        invoice.createdAt = createdAt;
        invoice.updatedAt = updatedAt;
        return invoice;
    }
    
    // Setters (package-private for domain operations and persistence)
    void setId(UUID id) {
        this.id = id;
    }
    
    void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
    
    void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    
    void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    
    void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    void setPaymentPlan(PaymentPlan paymentPlan) {
        this.paymentPlan = paymentPlan;
    }
    
    void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
    
    void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
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
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", status=" + status +
                ", total=" + calculateTotal() +
                ", balance=" + calculateBalance() +
                '}';
    }
}

