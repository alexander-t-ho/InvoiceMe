package com.invoiceme.application.invoices.listByStatus;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentCommand;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentHandler;
import com.invoiceme.application.customers.listAll.PagedResult;
import com.invoiceme.domain.invoices.InvoiceStatus;
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
class ListInvoicesByStatusHandlerTest {
    
    @Autowired
    private ListInvoicesByStatusHandler handler;
    
    @Autowired
    private CreateCustomerHandler createCustomerHandler;
    
    @Autowired
    private CreateInvoiceHandler createInvoiceHandler;
    
    @Autowired
    private AddLineItemHandler addLineItemHandler;
    
    @Autowired
    private MarkInvoiceAsSentHandler markInvoiceAsSentHandler;
    
    private UUID customerId;
    private UUID draftInvoiceId1;
    private UUID draftInvoiceId2;
    private UUID sentInvoiceId;
    
    @BeforeEach
    void setUp() {
        // Create customer
        CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(
            "John Doe",
            "john@example.com",
            "123 Main St"
        );
        customerId = createCustomerHandler.handle(createCustomerCommand);
        
        // Create draft invoices
        CreateInvoiceCommand createInvoice1 = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        draftInvoiceId1 = createInvoiceHandler.handle(createInvoice1);
        addLineItemHandler.handle(new AddLineItemCommand(
            draftInvoiceId1,
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        ));
        
        CreateInvoiceCommand createInvoice2 = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        draftInvoiceId2 = createInvoiceHandler.handle(createInvoice2);
        addLineItemHandler.handle(new AddLineItemCommand(
            draftInvoiceId2,
            "Service",
            BigDecimal.valueOf(5),
            BigDecimal.valueOf(200)
        ));
        
        // Create sent invoice
        CreateInvoiceCommand createInvoice3 = new CreateInvoiceCommand(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        sentInvoiceId = createInvoiceHandler.handle(createInvoice3);
        addLineItemHandler.handle(new AddLineItemCommand(
            sentInvoiceId,
            "Service",
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(300)
        ));
        markInvoiceAsSentHandler.handle(new MarkInvoiceAsSentCommand(sentInvoiceId));
    }
    
    @Test
    void shouldListDraftInvoices() {
        // Given
        ListInvoicesByStatusQuery query = new ListInvoicesByStatusQuery(
            InvoiceStatus.DRAFT,
            0,
            10
        );
        
        // When
        PagedResult<InvoiceSummaryDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(2, result.totalElements());
        assertEquals(InvoiceStatus.DRAFT, result.content().get(0).status());
        assertEquals(InvoiceStatus.DRAFT, result.content().get(1).status());
    }
    
    @Test
    void shouldListSentInvoices() {
        // Given
        ListInvoicesByStatusQuery query = new ListInvoicesByStatusQuery(
            InvoiceStatus.SENT,
            0,
            10
        );
        
        // When
        PagedResult<InvoiceSummaryDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals(sentInvoiceId, result.content().get(0).id());
        assertEquals(InvoiceStatus.SENT, result.content().get(0).status());
    }
    
    @Test
    void shouldHandlePagination() {
        // Given
        ListInvoicesByStatusQuery query = new ListInvoicesByStatusQuery(
            InvoiceStatus.DRAFT,
            0,
            1
        );
        
        // When
        PagedResult<InvoiceSummaryDto> result = handler.handle(query);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(2, result.totalElements());
        assertEquals(2, result.totalPages());
        assertTrue(result.hasNext());
    }
}

