package com.invoiceme.application.invoices.create;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.invoices.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CreateInvoiceHandlerTest {
    
    @Autowired
    private CreateInvoiceHandler handler;
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    private UUID customerId;
    
    @BeforeEach
    void setUp() {
        CreateCustomerCommand createCommand = new CreateCustomerCommand(
            "John Doe",
            "john@example.com",
            "123 Main St"
        );
        customerId = createCustomerHandler.handle(createCommand);
    }
    
    @Test
    void shouldCreateInvoiceInDraftStatus() {
        // Given
        CreateInvoiceCommand command = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        
        // When
        UUID invoiceId = handler.handle(command);
        
        // Then
        assertNotNull(invoiceId);
        var invoice = invoiceRepository.findById(invoiceId);
        assertTrue(invoice.isPresent());
        assertEquals(InvoiceStatus.DRAFT, invoice.get().getStatus());
        assertEquals(customerId, invoice.get().getCustomerId());
        assertTrue(invoice.get().canBeEdited());
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        CreateInvoiceCommand command = new CreateInvoiceCommand(
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(command);
        });
    }
}

