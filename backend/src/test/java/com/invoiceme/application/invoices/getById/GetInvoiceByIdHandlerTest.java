package com.invoiceme.application.invoices.getById;

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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GetInvoiceByIdHandlerTest {
    
    @Autowired
    private GetInvoiceByIdHandler handler;
    
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
        
        // Add line items
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId,
            "Service 1",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        ));
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId,
            "Service 2",
            BigDecimal.valueOf(5),
            BigDecimal.valueOf(200)
        ));
    }
    
    @Test
    void shouldGetInvoiceByIdSuccessfully() {
        // Given
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(invoiceId);
        
        // When
        InvoiceDto result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(invoiceId, result.id());
        assertEquals(customerId, result.customerId());
        assertEquals("John Doe", result.customerName());
        assertEquals(InvoiceStatus.DRAFT, result.status());
        assertEquals(2, result.lineItems().size());
        assertEquals(BigDecimal.valueOf(2000), result.totalAmount()); // (10×100) + (5×200)
        assertEquals(BigDecimal.valueOf(2000), result.balance()); // No payments yet
        assertEquals(0, result.payments().size());
    }
    
    @Test
    void shouldIncludePaymentsInInvoiceDto() {
        // Given - Mark as sent and record payment
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(invoiceId));
        recordPaymentHandler.handle(new RecordPaymentCommand(
            invoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "BANK_TRANSFER"
        ));
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(invoiceId);
        
        // When
        InvoiceDto result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.payments().size());
        assertEquals(BigDecimal.valueOf(500), result.payments().get(0).amount());
        assertEquals(BigDecimal.valueOf(1500), result.balance()); // 2000 - 500
    }
    
    @Test
    void shouldThrowExceptionWhenInvoiceNotFound() {
        // Given
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(UUID.randomUUID());
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(query);
        });
    }
}

