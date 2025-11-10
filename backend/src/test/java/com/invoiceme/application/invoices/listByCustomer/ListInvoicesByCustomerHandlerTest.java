package com.invoiceme.application.invoices.listByCustomer;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.customers.listAll.PagedResult;
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
class ListInvoicesByCustomerHandlerTest {
    
    @Autowired
    private ListInvoicesByCustomerHandler handler;
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private CreateInvoiceHandler createInvoiceHandler;
    
    @Autowired
    private AddLineItemHandler addLineItemHandler;
    
    private UUID customerId1;
    private UUID customerId2;
    private UUID invoiceId1;
    private UUID invoiceId2;
    private UUID invoiceId3;
    
    @BeforeEach
    void setUp() {
        // Create customers
        CreateCustomerCommand createCustomer1 = new CreateCustomerCommand(
            "John Doe",
            "john@example.com",
            "123 Main St"
        );
        customerId1 = createCustomerHandler.handle(createCustomer1);
        
        CreateCustomerCommand createCustomer2 = new CreateCustomerCommand(
            "Jane Smith",
            "jane@example.com",
            "456 Oak Ave"
        );
        customerId2 = createCustomerHandler.handle(createCustomer2);
        
        // Create invoices for customer 1
        CreateInvoiceCommand createInvoice1 = new CreateInvoiceCommand(
            customerId1,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        invoiceId1 = createInvoiceHandler.handle(createInvoice1);
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId1,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        ));
        
        CreateInvoiceCommand createInvoice2 = new CreateInvoiceCommand(
            customerId1,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        invoiceId2 = createInvoiceHandler.handle(createInvoice2);
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId2,
            "Service",
            BigDecimal.valueOf(5),
            BigDecimal.valueOf(200)
        ));
        
        // Create invoice for customer 2
        CreateInvoiceCommand createInvoice3 = new CreateInvoiceCommand(
            customerId2,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        invoiceId3 = createInvoiceHandler.handle(createInvoice3);
        addLineItemHandler.handle(new AddLineItemCommand(
            invoiceId3,
            "Service",
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(300)
        ));
    }
    
    @Test
    void shouldListInvoicesByCustomer() {
        // Given
        ListInvoicesByCustomerQuery query = new ListInvoicesByCustomerQuery(
            customerId1,
            0,
            10
        );
        
        // When
        PagedResult<com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(2, result.totalElements());
        assertEquals(customerId1, result.content().get(0).customerId());
        assertEquals(customerId1, result.content().get(1).customerId());
        assertEquals("John Doe", result.content().get(0).customerName());
    }
    
    @Test
    void shouldOnlyReturnInvoicesForSpecifiedCustomer() {
        // Given
        ListInvoicesByCustomerQuery query = new ListInvoicesByCustomerQuery(
            customerId2,
            0,
            10
        );
        
        // When
        PagedResult<com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals(customerId2, result.content().get(0).customerId());
        assertEquals(invoiceId3, result.content().get(0).id());
    }
    
    @Test
    void shouldHandlePagination() {
        // Given
        ListInvoicesByCustomerQuery query = new ListInvoicesByCustomerQuery(
            customerId1,
            0,
            1
        );
        
        // When
        PagedResult<com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(2, result.totalElements());
        assertEquals(2, result.totalPages());
        assertTrue(result.hasNext());
    }
}

