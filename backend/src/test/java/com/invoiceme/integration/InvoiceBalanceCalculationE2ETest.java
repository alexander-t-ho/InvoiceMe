package com.invoiceme.integration;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentCommand;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentHandler;
import com.invoiceme.application.payments.record.RecordPaymentCommand;
import com.invoiceme.application.payments.record.RecordPaymentHandler;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for invoice balance calculation accuracy.
 * Tests: Multiple line items → Multiple payments → Balance accuracy
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Invoice Balance Calculation E2E Tests")
class InvoiceBalanceCalculationE2ETest {
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private CreateInvoiceHandler createInvoiceHandler;
    
    @Autowired
    private AddLineItemHandler addLineItemHandler;
    
    @Autowired
    private MarkInvoiceAsSentHandler markInvoiceAsSentHandler;
    
    @Autowired
    private RecordPaymentHandler recordPaymentHandler;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    private UUID customerId;
    private UUID invoiceId;
    
    @BeforeEach
    void setUp() {
        // Create customer
        CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(
            "Balance Test Customer",
            "balance@example.com",
            "123 Test St"
        );
        customerId = createCustomerHandler.handle(createCustomerCommand);
        
        // Create invoice
        CreateInvoiceCommand createInvoiceCommand = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        invoiceId = createInvoiceHandler.handle(createInvoiceCommand);
    }
    
    @Test
    @DisplayName("Should calculate total correctly with multiple line items")
    void shouldCalculateTotalCorrectlyWithMultipleLineItems() {
        // Add multiple line items
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Item 1", BigDecimal.valueOf(5), BigDecimal.valueOf(100)
        ));
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Item 2", BigDecimal.valueOf(10), BigDecimal.valueOf(200)
        ));
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Item 3", BigDecimal.valueOf(3), BigDecimal.valueOf(150)
        ));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        
        // Expected: (5×100) + (10×200) + (3×150) = 500 + 2000 + 450 = 2950
        BigDecimal expectedTotal = BigDecimal.valueOf(2950);
        assertEquals(expectedTotal, invoice.calculateTotal());
    }
    
    @Test
    @DisplayName("Should calculate balance correctly after single payment")
    void shouldCalculateBalanceCorrectlyAfterSinglePayment() {
        // Setup: Add line items and mark as sent
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal total = invoice.calculateTotal(); // 1000
        
        // Record payment
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(600), LocalDate.now(), "BANK_TRANSFER"
        ));
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal balance = invoice.calculateBalance();
        
        // Expected: 1000 - 600 = 400
        assertEquals(BigDecimal.valueOf(400), balance);
        assertEquals(total.subtract(BigDecimal.valueOf(600)), balance);
    }
    
    @Test
    @DisplayName("Should calculate balance correctly after multiple payments")
    void shouldCalculateBalanceCorrectlyAfterMultiplePayments() {
        // Setup: Add line items and mark as sent
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Service", BigDecimal.valueOf(20), BigDecimal.valueOf(100)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal total = invoice.calculateTotal(); // 2000
        
        // Record first payment
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(500), LocalDate.now(), "BANK_TRANSFER"
        ));
        
        // Record second payment
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(800), LocalDate.now(), "CASH"
        ));
        
        // Record third payment
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(300), LocalDate.now(), "CHECK"
        ));
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal balance = invoice.calculateBalance();
        
        // Expected: 2000 - (500 + 800 + 300) = 2000 - 1600 = 400
        BigDecimal expectedBalance = total.subtract(BigDecimal.valueOf(1600));
        assertEquals(expectedBalance, balance);
        assertEquals(BigDecimal.valueOf(400), balance);
    }
    
    @Test
    @DisplayName("Should have zero balance when fully paid")
    void shouldHaveZeroBalanceWhenFullyPaid() {
        // Setup: Add line items and mark as sent
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal total = invoice.calculateTotal(); // 1000
        
        // Record full payment
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, total, LocalDate.now(), "BANK_TRANSFER"
        ));
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal balance = invoice.calculateBalance();
        
        // Expected: 0
        assertEquals(BigDecimal.ZERO, balance);
    }
    
    @Test
    @DisplayName("Should maintain correct balance with partial payments")
    void shouldMaintainCorrectBalanceWithPartialPayments() {
        // Setup: Add line items and mark as sent
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Service A", BigDecimal.valueOf(5), BigDecimal.valueOf(100)
        ));
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Service B", BigDecimal.valueOf(10), BigDecimal.valueOf(200)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal total = invoice.calculateTotal(); // (5×100) + (10×200) = 2500
        
        // Record partial payment 1
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(1000), LocalDate.now(), "BANK_TRANSFER"
        ));
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.valueOf(1500), invoice.calculateBalance());
        
        // Record partial payment 2
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(500), LocalDate.now(), "CASH"
        ));
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.valueOf(1000), invoice.calculateBalance());
        
        // Record final payment
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(1000), LocalDate.now(), "CHECK"
        ));
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
    }
}

