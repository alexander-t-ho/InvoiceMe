package com.invoiceme.application.invoices;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentCommand;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentHandler;
import com.invoiceme.application.invoices.removeLineItem.RemoveLineItemCommand;
import com.invoiceme.application.invoices.removeLineItem.RemoveLineItemHandler;
import com.invoiceme.application.invoices.update.UpdateInvoiceCommand;
import com.invoiceme.application.invoices.update.UpdateInvoiceHandler;
import com.invoiceme.application.payments.record.RecordPaymentCommand;
import com.invoiceme.application.payments.record.RecordPaymentHandler;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.exceptions.InsufficientPaymentException;
import com.invoiceme.domain.exceptions.InvalidInvoiceStateException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.invoices.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
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
 * Integration test for complete invoice lifecycle.
 * Tests: Create → Add Line Items → Update → Mark as Sent → Record Payment → Paid
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InvoiceLifecycleIntegrationTest {
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private CreateInvoiceHandler createInvoiceHandler;
    
    @Autowired
    private AddLineItemHandler addLineItemHandler;
    
    @Autowired
    private RemoveLineItemHandler removeLineItemHandler;
    
    @Autowired
    private UpdateInvoiceHandler updateInvoiceHandler;
    
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
            "John Doe",
            "john@example.com",
            "123 Main St"
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
    void shouldCompleteFullInvoiceLifecycle() {
        // Step 1: Add line items
        AddLineItemCommand addLineItem1 = new AddLineItemCommand(
            invoiceId,
            "Consulting Services",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(150)
        );
        addLineItemHandler.handle(addLineItem1);
        
        AddLineItemCommand addLineItem2 = new AddLineItemCommand(
            invoiceId,
            "Development Services",
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(200)
        );
        addLineItemHandler.handle(addLineItem2);
        
        // Verify invoice state
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(2, invoice.getLineItems().size());
        assertEquals(BigDecimal.valueOf(5500), invoice.calculateTotal()); // (10×150) + (20×200)
        assertEquals(InvoiceStatus.DRAFT, invoice.getStatus());
        
        // Get line item IDs for later removal
        UUID lineItem1Id = invoice.getLineItems().get(0).getId();
        
        // Step 2: Update invoice dates
        UpdateInvoiceCommand updateCommand = new UpdateInvoiceCommand(
            invoiceId,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(31)
        );
        updateInvoiceHandler.handle(updateCommand);
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(LocalDate.now().plusDays(1), invoice.getIssueDate());
        
        // Step 3: Remove a line item
        RemoveLineItemCommand removeCommand = new RemoveLineItemCommand(
            invoiceId,
            lineItem1Id
        );
        removeLineItemHandler.handle(removeCommand);
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(1, invoice.getLineItems().size());
        assertEquals(BigDecimal.valueOf(4000), invoice.calculateTotal()); // (20×200)
        
        // Step 4: Mark invoice as sent
        MarkInvoiceAsSentCommand markAsSentCommand = new MarkInvoiceAsSentCommand(invoiceId);
        markInvoiceAsSentHandler.handle(markAsSentCommand);
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(InvoiceStatus.SENT, invoice.getStatus());
        assertFalse(invoice.canBeEdited());
        
        // Step 5: Record partial payment
        RecordPaymentCommand payment1Command = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(2000),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        recordPaymentHandler.handle(payment1Command);
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.valueOf(2000), invoice.calculateBalance()); // 4000 - 2000
        assertEquals(InvoiceStatus.SENT, invoice.getStatus()); // Still SENT, not fully paid
        
        // Step 6: Record remaining payment
        RecordPaymentCommand payment2Command = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(2000),
            LocalDate.now(),
            "CASH"
        );
        recordPaymentHandler.handle(payment2Command);
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(InvoiceStatus.PAID, invoice.getStatus()); // Auto-transitioned to PAID
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
    }
    
    @Test
    void shouldNotAllowAddingLineItemAfterSent() {
        // Given - Create and send invoice
        AddLineItemCommand addLineItem = new AddLineItemCommand(
            invoiceId,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        addLineItemHandler.handle(addLineItem);
        
        MarkInvoiceAsSentCommand markAsSent = new MarkInvoiceAsSentCommand(invoiceId);
        markInvoiceAsSentHandler.handle(markAsSent);
        
        // When/Then - Try to add line item
        AddLineItemCommand addAfterSent = new AddLineItemCommand(
            invoiceId,
            "Another Service",
            BigDecimal.valueOf(5),
            BigDecimal.valueOf(50)
        );
        
        assertThrows(InvalidInvoiceStateException.class, () -> {
            addLineItemHandler.handle(addAfterSent);
        });
    }
    
    @Test
    void shouldNotAllowMarkingAsSentWithoutLineItems() {
        // Given - Invoice with no line items
        MarkInvoiceAsSentCommand command = new MarkInvoiceAsSentCommand(invoiceId);
        
        // When/Then
        assertThrows(InvalidInvoiceStateException.class, () -> {
            markInvoiceAsSentHandler.handle(command);
        });
    }
    
    @Test
    void shouldNotAllowPaymentExceedingBalance() {
        // Given - Invoice with line items, marked as sent
        AddLineItemCommand addLineItem = new AddLineItemCommand(
            invoiceId,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        addLineItemHandler.handle(addLineItem);
        
        MarkInvoiceAsSentCommand markAsSent = new MarkInvoiceAsSentCommand(invoiceId);
        markInvoiceAsSentHandler.handle(markAsSent);
        
        // When/Then - Try to pay more than balance
        RecordPaymentCommand overPayment = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(2000), // More than 1000
            LocalDate.now(),
            "CASH"
        );
        
        assertThrows(InsufficientPaymentException.class, () -> {
            recordPaymentHandler.handle(overPayment);
        });
    }
    
    @Test
    void shouldNotAllowPaymentForDraftInvoice() {
        // Given - Invoice in DRAFT status with line items
        AddLineItemCommand addLineItem = new AddLineItemCommand(
            invoiceId,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        addLineItemHandler.handle(addLineItem);
        
        // When/Then - Try to record payment
        RecordPaymentCommand payment = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "CASH"
        );
        
        assertThrows(InvalidInvoiceStateException.class, () -> {
            recordPaymentHandler.handle(payment);
        });
    }
}

