package com.invoiceme.integration;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentCommand;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentHandler;
import com.invoiceme.application.payments.getById.GetPaymentByIdHandler;
import com.invoiceme.application.payments.getById.GetPaymentByIdQuery;
import com.invoiceme.application.payments.listByInvoice.ListPaymentsByInvoiceHandler;
import com.invoiceme.application.payments.listByInvoice.ListPaymentsByInvoiceQuery;
import com.invoiceme.application.payments.record.RecordPaymentCommand;
import com.invoiceme.application.payments.record.RecordPaymentHandler;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.invoices.InvoiceStatus;
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
 * End-to-end integration test for payment application flow.
 * Tests: Create invoice → Send → Record payments → Verify tracking
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Payment Application Flow E2E Tests")
class PaymentApplicationFlowE2ETest {
    
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
    private GetPaymentByIdHandler getPaymentByIdHandler;
    
    @Autowired
    private ListPaymentsByInvoiceHandler listPaymentsByInvoiceHandler;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    private UUID customerId;
    private UUID invoiceId;
    
    @BeforeEach
    void setUp() {
        // Create customer
        CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(
            "Payment Test Customer",
            "payment@example.com",
            "456 Payment St"
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
    @DisplayName("Should complete payment application flow: Invoice → Send → Partial Payment → Full Payment → Paid")
    void shouldCompletePaymentApplicationFlow() {
        // Step 1: Add line items
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Consulting", BigDecimal.valueOf(10), BigDecimal.valueOf(150)
        ));
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Development", BigDecimal.valueOf(20), BigDecimal.valueOf(200)
        ));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal total = invoice.calculateTotal(); // (10×150) + (20×200) = 5500
        assertEquals(InvoiceStatus.DRAFT, invoice.getStatus());
        
        // Step 2: Mark invoice as sent
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(InvoiceStatus.SENT, invoice.getStatus());
        assertEquals(total, invoice.calculateBalance());
        
        // Step 3: Record partial payment
        RecordPaymentCommand partialPayment = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(3000),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        UUID payment1Id = recordPaymentHandler.handle(partialPayment);
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(InvoiceStatus.SENT, invoice.getStatus()); // Still SENT
        assertEquals(BigDecimal.valueOf(2500), invoice.calculateBalance()); // 5500 - 3000
        
        // Verify payment was recorded
        GetPaymentByIdQuery getPaymentQuery = new GetPaymentByIdQuery(payment1Id);
        var paymentDto = getPaymentByIdHandler.handle(getPaymentQuery);
        assertNotNull(paymentDto);
        assertEquals(BigDecimal.valueOf(3000), paymentDto.amount());
        assertEquals("BANK_TRANSFER", paymentDto.paymentMethod());
        
        // Step 4: Record remaining payment
        RecordPaymentCommand finalPayment = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(2500),
            LocalDate.now(),
            "CASH"
        );
        UUID payment2Id = recordPaymentHandler.handle(finalPayment);
        
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(InvoiceStatus.PAID, invoice.getStatus()); // Auto-transitioned to PAID
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
        
        // Step 5: Verify all payments are tracked
        ListPaymentsByInvoiceQuery listPaymentsQuery = new ListPaymentsByInvoiceQuery(invoiceId);
        var payments = listPaymentsByInvoiceHandler.handle(listPaymentsQuery);
        
        assertEquals(2, payments.size());
        assertEquals(BigDecimal.valueOf(5500), 
            payments.stream()
                .map(p -> p.amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    
    @Test
    @DisplayName("Should track multiple payments correctly")
    void shouldTrackMultiplePaymentsCorrectly() {
        // Setup: Add line items and mark as sent
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal total = invoice.calculateTotal(); // 1000
        
        // Record multiple payments
        UUID payment1Id = recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(200), LocalDate.now(), "BANK_TRANSFER"
        ));
        UUID payment2Id = recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(300), LocalDate.now(), "CASH"
        ));
        UUID payment3Id = recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(500), LocalDate.now(), "CHECK"
        ));
        
        // Verify all payments are tracked
        ListPaymentsByInvoiceQuery query = new ListPaymentsByInvoiceQuery(invoiceId);
        var payments = listPaymentsByInvoiceHandler.handle(query);
        
        assertEquals(3, payments.size());
        
        // Verify each payment
        var payment1 = getPaymentByIdHandler.handle(new GetPaymentByIdQuery(payment1Id));
        var payment2 = getPaymentByIdHandler.handle(new GetPaymentByIdQuery(payment2Id));
        var payment3 = getPaymentByIdHandler.handle(new GetPaymentByIdQuery(payment3Id));
        
        assertEquals(BigDecimal.valueOf(200), payment1.amount());
        assertEquals(BigDecimal.valueOf(300), payment2.amount());
        assertEquals(BigDecimal.valueOf(500), payment3.amount());
        
        // Verify final balance
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }
    
    @Test
    @DisplayName("Should update invoice balance after each payment")
    void shouldUpdateInvoiceBalanceAfterEachPayment() {
        // Setup: Add line items and mark as sent
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId, "Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        BigDecimal total = invoice.calculateTotal(); // 1000
        assertEquals(total, invoice.calculateBalance());
        
        // Payment 1: 200
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(200), LocalDate.now(), "BANK_TRANSFER"
        ));
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.valueOf(800), invoice.calculateBalance());
        
        // Payment 2: 300
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(300), LocalDate.now(), "CASH"
        ));
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.valueOf(500), invoice.calculateBalance());
        
        // Payment 3: 500 (final)
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId, BigDecimal.valueOf(500), LocalDate.now(), "CHECK"
        ));
        invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        assertEquals(BigDecimal.ZERO, invoice.calculateBalance());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }
}

