package com.invoiceme.application.customers.create;

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
class CreateCustomerHandlerTest {
    
    @Autowired
    private CreateCustomerHandler handler;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Test
    void shouldCreateCustomerSuccessfully() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Doe",
            "john@example.com",
            "123 Main St"
        );
        
        // When
        UUID customerId = handler.handle(command);
        
        // Then
        assertNotNull(customerId);
        var customer = customerRepository.findById(customerId);
        assertTrue(customer.isPresent());
        assertEquals("John Doe", customer.get().getName());
        assertEquals("john@example.com", customer.get().getEmail());
        assertEquals("123 Main St", customer.get().getAddress());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        CreateCustomerCommand firstCommand = new CreateCustomerCommand(
            "John Doe",
            "john@example.com",
            "123 Main St"
        );
        handler.handle(firstCommand);
        
        CreateCustomerCommand duplicateCommand = new CreateCustomerCommand(
            "Jane Doe",
            "john@example.com",
            "456 Oak Ave"
        );
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(duplicateCommand);
        });
    }
    
    @Test
    void shouldNormalizeEmailToLowerCase() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Doe",
            "JOHN@EXAMPLE.COM",
            "123 Main St"
        );
        
        // When
        UUID customerId = handler.handle(command);
        
        // Then
        var customer = customerRepository.findById(customerId);
        assertTrue(customer.isPresent());
        assertEquals("john@example.com", customer.get().getEmail());
    }
}

