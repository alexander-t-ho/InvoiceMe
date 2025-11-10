package com.invoiceme.api.invoices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceme.api.customers.CreateCustomerRequest;
import com.invoiceme.api.customers.CustomerResponse;
import com.invoiceme.domain.invoices.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InvoiceControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private UUID customerId;
    private UUID invoiceId;
    
    @BeforeEach
    void setUp() throws Exception {
        // Create a test customer
        CreateCustomerRequest customerRequest = new CreateCustomerRequest(
            "Test Customer",
            "test@example.com",
            "123 Test St"
        );
        
        String customerResponse = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        CustomerResponse customer = objectMapper.readValue(customerResponse, CustomerResponse.class);
        customerId = customer.id();
        
        // Create a test invoice
        CreateInvoiceRequest invoiceRequest = new CreateInvoiceRequest(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        
        String invoiceResponse = mockMvc.perform(post("/api/v1/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoiceRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        InvoiceResponse invoice = objectMapper.readValue(invoiceResponse, InvoiceResponse.class);
        invoiceId = invoice.id();
    }
    
    @Test
    void shouldCreateInvoice() throws Exception {
        CreateInvoiceRequest request = new CreateInvoiceRequest(
            customerId,
            LocalDate.now(),
            LocalDate.now().plusDays(30)
        );
        
        mockMvc.perform(post("/api/v1/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value(InvoiceStatus.DRAFT.name()))
                .andExpect(jsonPath("$.lineItems").isArray())
                .andExpect(jsonPath("$.payments").isArray())
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.balance").value(0));
    }
    
    @Test
    void shouldReturn400WhenCreatingInvoiceWithInvalidDueDate() throws Exception {
        CreateInvoiceRequest request = new CreateInvoiceRequest(
            customerId,
            LocalDate.now(),
            LocalDate.now().minusDays(1) // Past date
        );
        
        mockMvc.perform(post("/api/v1/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
    
    @Test
    void shouldUpdateInvoice() throws Exception {
        UpdateInvoiceRequest request = new UpdateInvoiceRequest(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(31)
        );
        
        mockMvc.perform(put("/api/v1/invoices/{id}", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invoiceId.toString()))
                .andExpect(jsonPath("$.issueDate").exists())
                .andExpect(jsonPath("$.dueDate").exists());
    }
    
    @Test
    void shouldAddLineItem() throws Exception {
        AddLineItemRequest request = new AddLineItemRequest(
            "Consulting Services",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(150)
        );
        
        mockMvc.perform(post("/api/v1/invoices/{id}/line-items", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lineItems.length()").value(1))
                .andExpect(jsonPath("$.lineItems[0].description").value("Consulting Services"))
                .andExpect(jsonPath("$.lineItems[0].quantity").value(10))
                .andExpect(jsonPath("$.lineItems[0].unitPrice").value(150))
                .andExpect(jsonPath("$.lineItems[0].total").value(1500))
                .andExpect(jsonPath("$.totalAmount").value(1500))
                .andExpect(jsonPath("$.balance").value(1500));
    }
    
    @Test
    void shouldReturn400WhenAddingLineItemWithInvalidQuantity() throws Exception {
        AddLineItemRequest request = new AddLineItemRequest(
            "Service",
            BigDecimal.valueOf(-1), // Invalid
            BigDecimal.valueOf(100)
        );
        
        mockMvc.perform(post("/api/v1/invoices/{id}/line-items", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
    
    @Test
    void shouldRemoveLineItem() throws Exception {
        // Add a line item first
        AddLineItemRequest addRequest = new AddLineItemRequest(
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        
        String invoiceResponse = mockMvc.perform(post("/api/v1/invoices/{id}/line-items", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        InvoiceResponse invoice = objectMapper.readValue(invoiceResponse, InvoiceResponse.class);
        UUID lineItemId = invoice.lineItems().get(0).id();
        
        // Remove the line item
        mockMvc.perform(delete("/api/v1/invoices/{id}/line-items/{lineItemId}", invoiceId, lineItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lineItems.length()").value(0))
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.balance").value(0));
    }
    
    @Test
    void shouldMarkInvoiceAsSent() throws Exception {
        // Add a line item first (required to send)
        AddLineItemRequest addRequest = new AddLineItemRequest(
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        
        mockMvc.perform(post("/api/v1/invoices/{id}/line-items", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());
        
        // Mark as sent
        mockMvc.perform(post("/api/v1/invoices/{id}/send", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(InvoiceStatus.SENT.name()));
    }
    
    @Test
    void shouldReturn400WhenMarkingInvoiceAsSentWithoutLineItems() throws Exception {
        mockMvc.perform(post("/api/v1/invoices/{id}/send", invoiceId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("line items")));
    }
    
    @Test
    void shouldGetInvoiceById() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/{id}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invoiceId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value(InvoiceStatus.DRAFT.name()))
                .andExpect(jsonPath("$.lineItems").isArray())
                .andExpect(jsonPath("$.payments").isArray());
    }
    
    @Test
    void shouldListInvoicesByStatus() throws Exception {
        // Mark invoice as sent
        AddLineItemRequest addRequest = new AddLineItemRequest(
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        
        mockMvc.perform(post("/api/v1/invoices/{id}/line-items", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/v1/invoices/{id}/send", invoiceId))
                .andExpect(status().isOk());
        
        // List SENT invoices
        mockMvc.perform(get("/api/v1/invoices")
                .param("status", InvoiceStatus.SENT.name())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value(InvoiceStatus.SENT.name()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }
    
    @Test
    void shouldListInvoicesByCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
                .param("customerId", customerId.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }
    
    @Test
    void shouldReturn400WhenUpdatingSentInvoice() throws Exception {
        // Add line item and mark as sent
        AddLineItemRequest addRequest = new AddLineItemRequest(
            "Service",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(100)
        );
        
        mockMvc.perform(post("/api/v1/invoices/{id}/line-items", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/v1/invoices/{id}/send", invoiceId))
                .andExpect(status().isOk());
        
        // Try to update (should fail)
        UpdateInvoiceRequest updateRequest = new UpdateInvoiceRequest(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(31)
        );
        
        mockMvc.perform(put("/api/v1/invoices/{id}", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("SENT")));
    }
}
