package com.invoiceme.application.customers.getById;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
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
class GetCustomerByIdHandlerTest {
    
    @Autowired
    private GetCustomerByIdHandler handler;
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    private UUID customerId;
    
    @BeforeEach
    void setUp() {
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Doe",
            "john@example.com",
            "123 Main St"
        );
        customerId = createCustomerHandler.handle(command);
    }
    
    @Test
    void shouldGetCustomerByIdSuccessfully() {
        // Given
        GetCustomerByIdQuery query = new GetCustomerByIdQuery(customerId);
        
        // When
        CustomerDto result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());
        assertEquals("123 Main St", result.address());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        GetCustomerByIdQuery query = new GetCustomerByIdQuery(UUID.randomUUID());
        
        // When/Then
        assertThrows(DomainValidationException.class, () -> {
            handler.handle(query);
        });
    }
}

