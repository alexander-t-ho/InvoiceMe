package com.invoiceme.application.payments.listByInvoice;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentCommand;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentHandler;
import com.invoiceme.application.invoices.getById.PaymentDto;
import com.invoiceme.application.payments.record.RecordPaymentCommand;
import com.invoiceme.application.payments.record.RecordPaymentHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ListPaymentsByInvoiceHandlerTest {
    
    @Autowired
    private ListPaymentsByInvoiceHandler handler;
    
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
        
        // Add line item and mark as sent
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
    }
    
    @Test
    void shouldListPaymentsByInvoice() {
        // Given - Record multiple payments (total invoice is 1000 = 10 * 100)
        LocalDate today = LocalDate.now();
        // First payment: 300 (balance: 700)
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(300),
            today,
            "BANK_TRANSFER"
        ));
        // Second payment: 400 (balance: 300, invoice still SENT)
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(400),
            today,
            "CASH"
        ));
        // Third payment: 300 (balance: 0, invoice becomes PAID)
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(300),
            today,
            "CHECK"
        ));
        
        ListPaymentsByInvoiceQuery query = new ListPaymentsByInvoiceQuery(invoiceId);
        
        // When
        List<PaymentDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        // Verify all payments are present (order may vary, so check by amount and method)
        assertTrue(result.stream().anyMatch(p -> p.amount().equals(BigDecimal.valueOf(300)) && p.paymentMethod().equals("BANK_TRANSFER")));
        assertTrue(result.stream().anyMatch(p -> p.amount().equals(BigDecimal.valueOf(400)) && p.paymentMethod().equals("CASH")));
        assertTrue(result.stream().anyMatch(p -> p.amount().equals(BigDecimal.valueOf(300)) && p.paymentMethod().equals("CHECK")));
    }
    
    @Test
    void shouldReturnEmptyListWhenNoPayments() {
        // Given
        ListPaymentsByInvoiceQuery query = new ListPaymentsByInvoiceQuery(invoiceId);
        
        // When
        List<PaymentDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

