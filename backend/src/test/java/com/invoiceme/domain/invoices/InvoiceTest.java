package com.invoiceme.domain.invoices;

import com.invoiceme.domain.exceptions.InvalidInvoiceStateException;
import com.invoiceme.domain.exceptions.InvalidLineItemException;
import com.invoiceme.domain.payments.Payment;
import com.invoiceme.domain.payments.PaymentPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {
    
    private UUID customerId;
    private Invoice invoice;
    
    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        invoice = Invoice.create(customerId, LocalDate.now(), LocalDate.now().plusDays(30), PaymentPlan.FULL);
    }
    
    @Test
    void shouldCreateInvoiceInDraftStatus() {
        assertEquals(InvoiceStatus.DRAFT, invoice.getStatus());
        assertEquals(customerId, invoice.getCustomerId());
        assertTrue(invoice.canBeEdited());
    }
    
    @Test
    void shouldAddLineItem() {
        LineItem lineItem = LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        invoice.addLineItem(lineItem);
        
        assertEquals(1, invoice.getLineItems().size());
        assertEquals(BigDecimal.valueOf(1000), invoice.calculateTotal());
    }
    
    @Test
    void shouldRemoveLineItem() {
        LineItem lineItem = LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        invoice.addLineItem(lineItem);
        invoice.removeLineItem(lineItem.getId());
        
        assertEquals(0, invoice.getLineItems().size());
        assertEquals(BigDecimal.ZERO, invoice.calculateTotal());
    }
    
    @Test
    void shouldNotAddLineItemWhenNotDraft() {
        LineItem lineItem = LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        invoice.addLineItem(lineItem);
        invoice.markAsSent();
        
        assertThrows(InvalidInvoiceStateException.class, () -> {
            invoice.addLineItem(LineItem.create("Another Service", BigDecimal.ONE, BigDecimal.valueOf(50)));
        });
    }
    
    @Test
    void shouldMarkAsSentWhenHasLineItems() {
        invoice.addLineItem(LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)));
        invoice.markAsSent();
        
        assertEquals(InvoiceStatus.SENT, invoice.getStatus());
        assertFalse(invoice.canBeEdited());
    }
    
    @Test
    void shouldNotMarkAsSentWithoutLineItems() {
        assertThrows(InvalidInvoiceStateException.class, () -> {
            invoice.markAsSent();
        });
    }
    
    @Test
    void shouldCalculateBalanceCorrectly() {
        invoice.addLineItem(LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)));
        invoice.markAsSent();
        
        Payment payment = Payment.create(invoice.getId(), BigDecimal.valueOf(500), LocalDate.now(), "CASH");
        invoice.applyPayment(payment);
        
        assertEquals(BigDecimal.valueOf(500), invoice.calculateBalance());
    }
    
    @Test
    void shouldTransitionToPaidWhenBalanceIsZero() {
        invoice.addLineItem(LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)));
        invoice.markAsSent();
        
        Payment payment = Payment.create(invoice.getId(), BigDecimal.valueOf(1000), LocalDate.now(), "CASH");
        invoice.applyPayment(payment);
        
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
    }
    
    @Test
    void shouldNotAllowPaymentExceedingBalance() {
        invoice.addLineItem(LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)));
        invoice.markAsSent();
        
        Payment payment = Payment.create(invoice.getId(), BigDecimal.valueOf(1500), LocalDate.now(), "CASH");
        
        assertThrows(com.invoiceme.domain.exceptions.InsufficientPaymentException.class, () -> {
            invoice.applyPayment(payment);
        });
    }
}


