package com.invoiceme.application.customers.listAll;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.customers.getById.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ListAllCustomersHandlerTest {
    
    @Autowired
    private ListAllCustomersHandler handler;
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @BeforeEach
    void setUp() {
        // Create test customers
        createCustomerHandler.handle(new CreateCustomerCommand(
            "Alice Smith",
            "alice@example.com",
            "456 Oak Ave"
        ));
        createCustomerHandler.handle(new CreateCustomerCommand(
            "Bob Johnson",
            "bob@example.com",
            "789 Pine St"
        ));
        createCustomerHandler.handle(new CreateCustomerCommand(
            "Charlie Brown",
            "charlie@example.com",
            "321 Elm Dr"
        ));
    }
    
    @Test
    void shouldListAllCustomersWithPagination() {
        // Given
        ListAllCustomersQuery query = new ListAllCustomersQuery(0, 2, "name");
        
        // When
        PagedResult<CustomerDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(2, result.size());
        assertEquals(3, result.totalElements());
        assertEquals(2, result.totalPages());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
        
        // Verify sorted by name
        assertEquals("Alice Smith", result.content().get(0).name());
        assertEquals("Bob Johnson", result.content().get(1).name());
    }
    
    @Test
    void shouldHandleSecondPage() {
        // Given
        ListAllCustomersQuery query = new ListAllCustomersQuery(1, 2, "name");
        
        // When
        PagedResult<CustomerDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1, result.page());
        assertEquals(2, result.size());
        assertEquals(3, result.totalElements());
        assertEquals(2, result.totalPages());
        assertFalse(result.hasNext());
        assertTrue(result.hasPrevious());
        
        // Verify second page content
        assertEquals("Charlie Brown", result.content().get(0).name());
    }
    
    @Test
    void shouldUseDefaultPagination() {
        // Given
        ListAllCustomersQuery query = new ListAllCustomersQuery();
        
        // When
        PagedResult<CustomerDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.page());
        assertEquals(20, result.size());
        assertTrue(result.content().size() <= 20);
    }
}

