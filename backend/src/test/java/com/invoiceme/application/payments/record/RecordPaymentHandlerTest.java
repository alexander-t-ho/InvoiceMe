package com.invoiceme.application.payments.record;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentCommand;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentHandler;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.exceptions.InsufficientPaymentException;
import com.invoiceme.domain.exceptions.InvalidInvoiceStateException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.invoices.InvoiceStatus;
import com.invoiceme.domain.payments.PaymentRepository;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RecordPaymentHandlerTest {
    
    @Autowired
    private RecordPaymentHandler handler;
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private CreateInvoiceHandler createInvoiceHandler;
    
    @Autowired
    private AddLineItemHandler addLineItemHandler;
    
    @Autowired
    private MarkInvoiceAsSentHandler markInvoiceAsSentHandler;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
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
        
        // Create invoice with line items and mark as sent
        CreateInvoiceCommand createInvoiceCommand = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        invoiceId = createInvoiceHandler.handle(createInvoiceCommand);
        
        AddLineItemCommand addLineItem = new AddLineItemCommand(
            invoiceId,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        addLineItemHandler.handle(addLineItem);
        
        MarkInvoiceAsSentCommand markAsSent = new MarkInvoiceAsSentCommand(invoiceId);
        markInvoiceAsSentHandler.handle(markAsSent);
    }
    
    @Test
    void shouldRecordPaymentSuccessfully() {
        // Given
        RecordPaymentCommand command = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        // When
        UUID paymentId = handler.handle(command);
        
        // Then
        assertNotNull(paymentId);
        var payment = paymentRepository.findById(paymentId);
        assertTrue(payment.isPresent());
        assertEquals(invoiceId, payment.get().getInvoiceId());
        assertEquals(BigDecimal.valueOf(500), payment.get().getAmount());
        
        // Verify invoice balance updated
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.valueOf(500), invoice.calculateBalance()); // 1000 - 500
        assertEquals(InvoiceStatus.SENT, invoice.getStatus()); // Still SENT
    }
    
    @Test
    void shouldTransitionInvoiceToPaidWhenBalanceIsZero() {
        // Given
        RecordPaymentCommand command = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(1000), // Full payment
            LocalDate.now(),
            "CASH"
        );
        
        // When
        handler.handle(command);
        
        // Then
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
    }
    
    @Test
    void shouldThrowExceptionWhenInvoiceNotFound() {
        // Given
        RecordPaymentCommand command = new RecordPaymentCommand(
            UUID.randomUUID(),
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "CASH"
        );
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(command);
        });
    }
    
    @Test
    void shouldThrowExceptionWhenPaymentExceedsBalance() {
        // Given
        RecordPaymentCommand command = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(2000), // More than 1000
            LocalDate.now(),
            "CASH"
        );
        
        // When/Then
        assertThrows(InsufficientPaymentException.class, () -> {
            handler.handle(command);
        });
    }
    
    @Test
    void shouldThrowExceptionWhenInvoiceIsDraft() {
        // Given - Create a new invoice in DRAFT status
        CreateInvoiceCommand createInvoiceCommand = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        UUID draftInvoiceId = createInvoiceHandler.handle(createInvoiceCommand);
        
        AddLineItemCommand addLineItem = new AddLineItemCommand(
            draftInvoiceId,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        addLineItemHandler.handle(addLineItem);
        
        RecordPaymentCommand command = new RecordPaymentCommand(
            draftInvoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "CASH"
        );
        
        // When/Then
        assertThrows(InvalidInvoiceStateException.class, () -> {
            handler.handle(command);
        });
    }
    
    @Test
    void shouldHandleMultiplePayments() {
        // Given - Record first payment
        RecordPaymentCommand payment1 = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(300),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        handler.handle(payment1);
        
        // When - Record second payment
        RecordPaymentCommand payment2 = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(700),
            LocalDate.now(),
            "CASH"
        );
        handler.handle(payment2);
        
        // Then
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
        assertEquals(2, invoice.getPayments().size());
    }
}

