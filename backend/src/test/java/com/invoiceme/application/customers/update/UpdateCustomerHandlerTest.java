package com.invoiceme.application.customers.update;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UpdateCustomerHandlerTest {
    
    @Autowired
    private UpdateCustomerHandler handler;
    
    @Autowired
    private CreateCustomerHandler createHandler;
    
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
        customerId = createHandler.handle(createCommand);
    }
    
    @Test
    void shouldUpdateCustomerSuccessfully() {
        // Given
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            customerId,
            "Jane Doe",
            "jane@example.com",
            "456 Oak Ave"
        );
        
        // When
        handler.handle(command);
        
        // Then
        var customer = customerRepository.findById(customerId);
        assertTrue(customer.isPresent());
        assertEquals("Jane Doe", customer.get().getName());
        assertEquals("jane@example.com", customer.get().getEmail());
        assertEquals("456 Oak Ave", customer.get().getAddress());
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            UUID.randomUUID(),
            "Jane Doe",
            "jane@example.com",
            "456 Oak Ave"
        );
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(command);
        });
    }
    
    @Test
    void shouldThrowExceptionWhenNewEmailAlreadyExists() {
        // Given - Create another customer
        CreateCustomerCommand createCommand2 = new CreateCustomerCommand(
            "Bob Smith",
            "bob@example.com",
            "789 Pine St"
        );
        createHandler.handle(createCommand2);
        
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            customerId,
            "John Doe",
            "bob@example.com", // Try to use existing email
            "123 Main St"
        );
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(command);
        });
    }
}

