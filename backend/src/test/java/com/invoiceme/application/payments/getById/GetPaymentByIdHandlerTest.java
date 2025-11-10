package com.invoiceme.application.payments.getById;

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
import com.invoiceme.domain.exceptions.DomainValidationException;
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
class GetPaymentByIdHandlerTest {
    
    @Autowired
    private GetPaymentByIdHandler handler;
    
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
    private UUID paymentId;
    
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
        
        // Record payment
        RecordPaymentCommand recordPaymentCommand = new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        paymentId = recordPaymentHandler.handle(recordPaymentCommand);
    }
    
    @Test
    void shouldGetPaymentByIdSuccessfully() {
        // Given
        GetPaymentByIdQuery query = new GetPaymentByIdQuery(paymentId);
        
        // When
        PaymentDetailDto result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(paymentId, result.id());
        assertEquals(invoiceId, result.invoiceId());
        assertEquals(BigDecimal.valueOf(500), result.amount());
        assertEquals("BANK_TRANSFER", result.paymentMethod());
        assertNotNull(result.paymentDate());
        assertNotNull(result.createdAt());
    }
    
    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {
        // Given
        GetPaymentByIdQuery query = new GetPaymentByIdQuery(UUID.randomUUID());
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(query);
        });
    }
}

