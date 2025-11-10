package com.invoiceme.api.customers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private UUID customerId;
    
    @BeforeEach
    void setUp() throws Exception {
        // Create a test customer
        CreateCustomerRequest request = new CreateCustomerRequest(
            "Test Customer",
            "test@example.com",
            "123 Test St"
        );
        
        String response = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Customer"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        CustomerResponse customer = objectMapper.readValue(response, CustomerResponse.class);
        customerId = customer.id();
    }
    
    @Test
    void shouldCreateCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
            "John Doe",
            "john@example.com",
            "456 Oak Ave"
        );
        
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.address").value("456 Oak Ave"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }
    
    @Test
    void shouldReturn400WhenCreatingCustomerWithInvalidEmail() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
            "John Doe",
            "invalid-email",
            "456 Oak Ave"
        );
        
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    void shouldReturn400WhenCreatingCustomerWithMissingName() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
            null,
            "john@example.com",
            "456 Oak Ave"
        );
        
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }
    
    @Test
    void shouldUpdateCustomer() throws Exception {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
            "Updated Name",
            "updated@example.com",
            "789 Updated St"
        );
        
        mockMvc.perform(put("/api/v1/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.address").value("789 Updated St"));
    }
    
    @Test
    void shouldReturn404WhenUpdatingNonExistentCustomer() throws Exception {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
            "Updated Name",
            "updated@example.com",
            "789 Updated St"
        );
        
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(put("/api/v1/customers/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }
    
    @Test
    void shouldDeleteCustomer() throws Exception {
        // Create a customer to delete
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
            "To Delete",
            "delete@example.com",
            "Delete St"
        );
        
        String createResponse = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        CustomerResponse created = objectMapper.readValue(createResponse, CustomerResponse.class);
        
        // Delete the customer
        mockMvc.perform(delete("/api/v1/customers/{id}", created.id()))
                .andExpect(status().isNoContent());
        
        // Verify customer is deleted
        mockMvc.perform(get("/api/v1/customers/{id}", created.id()))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturn404WhenDeletingNonExistentCustomer() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(delete("/api/v1/customers/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
    
    @Test
    void shouldGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("Test Customer"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.address").value("123 Test St"));
    }
    
    @Test
    void shouldReturn404WhenGettingNonExistentCustomer() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }
    
    @Test
    void shouldListAllCustomers() throws Exception {
        // Create additional customers
        for (int i = 0; i < 3; i++) {
            CreateCustomerRequest request = new CreateCustomerRequest(
                "Customer " + i,
                "customer" + i + "@example.com",
                "Address " + i
            );
            mockMvc.perform(post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
        
        mockMvc.perform(get("/api/v1/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.hasNext").exists())
                .andExpect(jsonPath("$.hasPrevious").exists());
    }
    
    @Test
    void shouldListCustomersWithPagination() throws Exception {
        // Create 5 customers
        for (int i = 0; i < 5; i++) {
            CreateCustomerRequest request = new CreateCustomerRequest(
                "Customer " + i,
                "customer" + i + "@example.com",
                "Address " + i
            );
            mockMvc.perform(post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
        
        // First page
        mockMvc.perform(get("/api/v1/customers")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false));
        
        // Second page
        mockMvc.perform(get("/api/v1/customers")
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(true));
    }
}
