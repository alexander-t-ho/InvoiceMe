package com.invoiceme.domain.customers;

import com.invoiceme.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    
    @Test
    void shouldCreateCustomerWithValidData() {
        Customer customer = Customer.create("John Doe", "john@example.com", "123 Main St");
        
        assertNotNull(customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("123 Main St", customer.getAddress());
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(DomainValidationException.class, () -> {
            Customer.create(null, "john@example.com", "123 Main St");
        });
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(DomainValidationException.class, () -> {
            Customer.create("", "john@example.com", "123 Main St");
        });
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThrows(DomainValidationException.class, () -> {
            Customer.create("John Doe", "invalid-email", "123 Main St");
        });
    }
    
    @Test
    void shouldNormalizeEmailToLowerCase() {
        Customer customer = Customer.create("John Doe", "JOHN@EXAMPLE.COM", "123 Main St");
        assertEquals("john@example.com", customer.getEmail());
    }
    
    @Test
    void shouldUpdateCustomerDetails() {
        Customer customer = Customer.create("John Doe", "john@example.com", "123 Main St");
        customer.updateDetails("Jane Doe", "jane@example.com", "456 Oak Ave");
        
        assertEquals("Jane Doe", customer.getName());
        assertEquals("jane@example.com", customer.getEmail());
        assertEquals("456 Oak Ave", customer.getAddress());
    }
    
    @Test
    void shouldHaveEqualCustomersWithSameId() {
        Customer customer1 = Customer.create("John Doe", "john@example.com", "123 Main St");
        Customer customer2 = Customer.create("Jane Doe", "jane@example.com", "456 Oak Ave");
        
        // Set same ID for testing equality
        customer2.setId(customer1.getId());
        
        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }
}


