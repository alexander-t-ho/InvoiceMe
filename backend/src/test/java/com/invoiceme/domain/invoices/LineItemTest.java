package com.invoiceme.domain.invoices;

import com.invoiceme.domain.exceptions.InvalidLineItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LineItemTest {
    
    @Test
    void shouldCreateLineItemWithValidData() {
        LineItem lineItem = LineItem.create("Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        
        assertNotNull(lineItem.getId());
        assertEquals("Service", lineItem.getDescription());
        assertEquals(BigDecimal.valueOf(10), lineItem.getQuantity());
        assertEquals(BigDecimal.valueOf(100), lineItem.getUnitPrice());
        assertEquals(BigDecimal.valueOf(1000), lineItem.getTotal());
    }
    
    @Test
    void shouldCalculateTotalCorrectly() {
        LineItem lineItem = LineItem.create("Product", BigDecimal.valueOf(5), BigDecimal.valueOf(25.50));
        assertEquals(BigDecimal.valueOf(127.50), lineItem.getTotal());
    }
    
    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        assertThrows(InvalidLineItemException.class, () -> {
            LineItem.create(null, BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        });
    }
    
    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        assertThrows(InvalidLineItemException.class, () -> {
            LineItem.create("Service", BigDecimal.ZERO, BigDecimal.valueOf(100));
        });
    }
    
    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        assertThrows(InvalidLineItemException.class, () -> {
            LineItem.create("Service", BigDecimal.valueOf(-1), BigDecimal.valueOf(100));
        });
    }
    
    @Test
    void shouldAllowZeroUnitPrice() {
        LineItem lineItem = LineItem.create("Free Service", BigDecimal.valueOf(1), BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, lineItem.getTotal());
    }
    
    @Test
    void shouldCreateLineItemWithSpecificId() {
        UUID id = UUID.randomUUID();
        LineItem lineItem = LineItem.of(id, "Service", BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        
        assertEquals(id, lineItem.getId());
    }
}


