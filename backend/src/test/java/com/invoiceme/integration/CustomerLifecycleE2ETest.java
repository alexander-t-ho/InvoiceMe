package com.invoiceme.integration;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.customers.delete.DeleteCustomerCommand;
import com.invoiceme.application.customers.delete.DeleteCustomerHandler;
import com.invoiceme.application.customers.getById.GetCustomerByIdQuery;
import com.invoiceme.application.customers.getById.GetCustomerByIdHandler;
import com.invoiceme.application.customers.update.UpdateCustomerCommand;
import com.invoiceme.application.customers.update.UpdateCustomerHandler;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for complete Customer lifecycle.
 * Tests: Create → Get → Update → Delete
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Customer Lifecycle E2E Tests")
class CustomerLifecycleE2ETest {
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private GetCustomerByIdHandler getCustomerByIdHandler;
    
    @Autowired
    private UpdateCustomerHandler updateCustomerHandler;
    
    @Autowired
    private DeleteCustomerHandler deleteCustomerHandler;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    private UUID customerId;
    
    @BeforeEach
    void setUp() {
        // Create a customer for each test
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Jane Smith",
            "jane.smith@example.com",
            "456 Oak Avenue"
        );
        customerId = createCustomerHandler.handle(command);
    }
    
    @Test
    @DisplayName("Should complete full customer lifecycle: Create → Get → Update → Delete")
    void shouldCompleteFullCustomerLifecycle() {
        // Step 1: Verify customer was created
        assertNotNull(customerId);
        assertTrue(customerRepository.findById(customerId).isPresent());
        
        // Step 2: Get customer by ID
        GetCustomerByIdQuery getQuery = new GetCustomerByIdQuery(customerId);
        var customerDto = getCustomerByIdHandler.handle(getQuery);
        
        assertNotNull(customerDto);
        assertEquals("Jane Smith", customerDto.name());
        assertEquals("jane.smith@example.com", customerDto.email());
        assertEquals("456 Oak Avenue", customerDto.address());
        
        // Step 3: Update customer
        UpdateCustomerCommand updateCommand = new UpdateCustomerCommand(
            customerId,
            "Jane Smith-Jones",
            "jane.smith-jones@example.com",
            "789 Pine Street"
        );
        updateCustomerHandler.handle(updateCommand);
        
        // Verify update
        customerDto = getCustomerByIdHandler.handle(getQuery);
        assertEquals("Jane Smith-Jones", customerDto.name());
        assertEquals("jane.smith-jones@example.com", customerDto.email());
        assertEquals("789 Pine Street", customerDto.address());
        
        // Step 4: Delete customer (if no invoices)
        DeleteCustomerCommand deleteCommand = new DeleteCustomerCommand(customerId);
        deleteCustomerHandler.handle(deleteCommand);
        
        // Verify deletion
        assertFalse(customerRepository.findById(customerId).isPresent());
    }
    
    @Test
    @DisplayName("Should not allow duplicate email addresses")
    void shouldNotAllowDuplicateEmail() {
        // Try to create another customer with the same email
        CreateCustomerCommand duplicateCommand = new CreateCustomerCommand(
            "Another Name",
            "jane.smith@example.com", // Same email
            "Different Address"
        );
        
        assertThrows(DomainValidationException.class, () -> {
            createCustomerHandler.handle(duplicateCommand);
        });
    }
    
    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmailToLowerCase() {
        // Create customer with uppercase email
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Test User",
            "TEST.USER@EXAMPLE.COM",
            "123 Test St"
        );
        UUID newCustomerId = createCustomerHandler.handle(command);
        
        // Verify email is normalized
        GetCustomerByIdQuery query = new GetCustomerByIdQuery(newCustomerId);
        var customerDto = getCustomerByIdHandler.handle(query);
        
        assertEquals("test.user@example.com", customerDto.email());
    }
    
    @Test
    @DisplayName("Should update customer email and verify uniqueness")
    void shouldUpdateCustomerEmailWithUniquenessCheck() {
        // Create another customer
        CreateCustomerCommand command2 = new CreateCustomerCommand(
            "Other User",
            "other@example.com",
            "999 Other St"
        );
        UUID customer2Id = createCustomerHandler.handle(command2);
        
        // Try to update customer2's email to match customer1's email
        UpdateCustomerCommand updateCommand = new UpdateCustomerCommand(
            customer2Id,
            "Other User",
            "jane.smith@example.com", // Duplicate email
            "999 Other St"
        );
        
        assertThrows(DomainValidationException.class, () -> {
            updateCustomerHandler.handle(updateCommand);
        });
    }
}

