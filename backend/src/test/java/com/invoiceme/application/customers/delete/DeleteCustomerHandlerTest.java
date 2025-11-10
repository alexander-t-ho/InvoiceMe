package com.invoiceme.application.customers.delete;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
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
class DeleteCustomerHandlerTest {
    
    @Autowired
    private DeleteCustomerHandler handler;
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private CreateInvoiceHandler createInvoiceHandler;
    
    @Autowired
    private CustomerRepository customerRepository;
    
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
    void shouldDeleteCustomerSuccessfully() {
        // Given
        DeleteCustomerCommand command = new DeleteCustomerCommand(customerId);
        
        // When
        handler.handle(command);
        
        // Then
        assertFalse(customerRepository.existsById(customerId));
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        DeleteCustomerCommand command = new DeleteCustomerCommand(UUID.randomUUID());
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(command);
        });
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerHasInvoices() {
        // Given - Create an invoice for the customer
        CreateInvoiceCommand createInvoiceCommand = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        createInvoiceHandler.handle(createInvoiceCommand);
        
        DeleteCustomerCommand command = new DeleteCustomerCommand(customerId);
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(command);
        });
    }
}

