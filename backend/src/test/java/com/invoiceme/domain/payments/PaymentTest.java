package com.invoiceme.domain.payments;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.payments.PaymentPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    
    private UUID invoiceId;
    
    @BeforeEach
    void setUp() {
        invoiceId = UUID.randomUUID();
    }
    
    @Test
    void shouldCreatePaymentWithValidData() {
        Payment payment = Payment.create(invoiceId, BigDecimal.valueOf(100), LocalDate.now(), "CASH");
        
        assertNotNull(payment.getId());
        assertEquals(invoiceId, payment.getInvoiceId());
        assertEquals(BigDecimal.valueOf(100), payment.getAmount());
        assertEquals(LocalDate.now(), payment.getPaymentDate());
        assertEquals("CASH", payment.getPaymentMethod());
        assertNotNull(payment.getCreatedAt());
    }
    
    @Test
    void shouldThrowExceptionWhenAmountIsZero() {
        assertThrows(DomainValidationException.class, () -> {
            Payment.create(invoiceId, BigDecimal.ZERO, LocalDate.now(), "CASH");
        });
    }
    
    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(DomainValidationException.class, () -> {
            Payment.create(invoiceId, BigDecimal.valueOf(-1), LocalDate.now(), "CASH");
        });
    }
    
    @Test
    void shouldThrowExceptionWhenPaymentDateIsInFuture() {
        assertThrows(DomainValidationException.class, () -> {
            Payment.create(invoiceId, BigDecimal.valueOf(100), LocalDate.now().plusDays(1), "CASH");
        });
    }
    
    @Test
    void shouldValidateAgainstInvoice() {
        UUID customerId = UUID.randomUUID();
        Invoice invoice = Invoice.create(customerId, LocalDate.now(), LocalDate.now().plusDays(30), PaymentPlan.FULL);
        invoice.addLineItem(com.invoiceme.domain.invoices.LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)));
        invoice.markAsSent();
        
        Payment payment = Payment.create(invoice.getId(), BigDecimal.valueOf(500), LocalDate.now(), "CASH");
        
        // Should not throw exception
        assertDoesNotThrow(() -> payment.validateAgainstInvoice(invoice));
    }
    
    @Test
    void shouldThrowExceptionWhenPaymentExceedsInvoiceBalance() {
        UUID customerId = UUID.randomUUID();
        Invoice invoice = Invoice.create(customerId, LocalDate.now(), LocalDate.now().plusDays(30), PaymentPlan.FULL);
        invoice.addLineItem(com.invoiceme.domain.invoices.LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)));
        invoice.markAsSent();
        
        Payment payment = Payment.create(invoice.getId(), BigDecimal.valueOf(1500), LocalDate.now(), "CASH");
        
        assertThrows(com.invoiceme.domain.exceptions.InsufficientPaymentException.class, () -> {
            payment.validateAgainstInvoice(invoice);
        });
    }
}


