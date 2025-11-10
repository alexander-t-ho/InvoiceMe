package com.invoiceme.api.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceme.api.customers.CreateCustomerRequest;
import com.invoiceme.api.customers.CustomerResponse;
import com.invoiceme.api.invoices.AddLineItemRequest;
import com.invoiceme.api.invoices.CreateInvoiceRequest;
import com.invoiceme.api.invoices.InvoiceResponse;
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
class PaymentControllerIntegrationTest {
    
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
        
        // Create and send an invoice
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
        
        // Add line item
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
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldRecordPayment() throws Exception {
        RecordPaymentRequest request = new RecordPaymentRequest(
            invoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.invoiceId").value(invoiceId.toString()))
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.paymentMethod").value("BANK_TRANSFER"))
                .andExpect(jsonPath("$.paymentDate").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }
    
    @Test
    void shouldReturn400WhenRecordingPaymentExceedingBalance() throws Exception {
        RecordPaymentRequest request = new RecordPaymentRequest(
            invoiceId,
            BigDecimal.valueOf(2000), // More than 1000
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("exceeds")));
    }
    
    @Test
    void shouldReturn400WhenRecordingPaymentForDraftInvoice() throws Exception {
        // Create a draft invoice
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
        UUID draftInvoiceId = invoice.id();
        
        // Try to record payment (should fail)
        RecordPaymentRequest request = new RecordPaymentRequest(
            draftInvoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("DRAFT")));
    }
    
    @Test
    void shouldReturn400WhenRecordingPaymentWithInvalidAmount() throws Exception {
        RecordPaymentRequest request = new RecordPaymentRequest(
            invoiceId,
            BigDecimal.valueOf(-100), // Invalid
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
    
    @Test
    void shouldGetPaymentById() throws Exception {
        // Record a payment first
        RecordPaymentRequest createRequest = new RecordPaymentRequest(
            invoiceId,
            BigDecimal.valueOf(500),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        String paymentResponse = mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PaymentDetailResponse payment = objectMapper.readValue(paymentResponse, PaymentDetailResponse.class);
        UUID paymentId = payment.id();
        
        // Get payment by ID
        mockMvc.perform(get("/api/v1/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.invoiceId").value(invoiceId.toString()))
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.paymentMethod").value("BANK_TRANSFER"));
    }
    
    @Test
    void shouldReturn404WhenGettingNonExistentPayment() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/payments/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }
    
    @Test
    void shouldListPaymentsByInvoice() throws Exception {
        // Record multiple payments
        RecordPaymentRequest payment1 = new RecordPaymentRequest(
            invoiceId,
            BigDecimal.valueOf(300),
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        RecordPaymentRequest payment2 = new RecordPaymentRequest(
            invoiceId,
            BigDecimal.valueOf(400),
            LocalDate.now(),
            "CASH"
        );
        
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment2)))
                .andExpect(status().isCreated());
        
        // List payments
        mockMvc.perform(get("/api/v1/payments/invoices/{invoiceId}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].invoiceId").value(invoiceId.toString()))
                .andExpect(jsonPath("$[1].invoiceId").value(invoiceId.toString()));
    }
    
    @Test
    void shouldTransitionInvoiceToPaidWhenBalanceIsZero() throws Exception {
        // Record full payment
        RecordPaymentRequest request = new RecordPaymentRequest(
            invoiceId,
            BigDecimal.valueOf(1000), // Full amount
            LocalDate.now(),
            "BANK_TRANSFER"
        );
        
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        
        // Verify invoice is now PAID
        mockMvc.perform(get("/api/v1/invoices/{id}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.balance").value(0));
    }
}
