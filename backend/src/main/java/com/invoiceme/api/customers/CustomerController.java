package com.invoiceme.api.customers;

import com.invoiceme.application.customers.create.CreateCustomerCommand;
import com.invoiceme.application.customers.create.CreateCustomerHandler;
import com.invoiceme.application.customers.delete.DeleteCustomerCommand;
import com.invoiceme.application.customers.delete.DeleteCustomerHandler;
import com.invoiceme.application.customers.getById.GetCustomerByIdQuery;
import com.invoiceme.application.customers.getById.GetCustomerByIdHandler;
import com.invoiceme.application.customers.getByEmail.GetCustomerByEmailQuery;
import com.invoiceme.application.customers.getByEmail.GetCustomerByEmailHandler;
import com.invoiceme.application.customers.authenticate.AuthenticateCustomerCommand;
import com.invoiceme.application.customers.authenticate.AuthenticateCustomerHandler;
import com.invoiceme.application.customers.listAll.ListAllCustomersQuery;
import com.invoiceme.application.customers.listAll.ListAllCustomersHandler;
import com.invoiceme.application.customers.update.UpdateCustomerCommand;
import com.invoiceme.application.customers.update.UpdateCustomerHandler;
import com.invoiceme.application.customers.listAll.PagedResult;
import com.invoiceme.api.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Customer operations.
 */
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management API")
public class CustomerController {
    
    private final CreateCustomerHandler createCustomerHandler;
    private final UpdateCustomerHandler updateCustomerHandler;
    private final DeleteCustomerHandler deleteCustomerHandler;
    private final GetCustomerByIdHandler getCustomerByIdHandler;
    private final GetCustomerByEmailHandler getCustomerByEmailHandler;
    private final AuthenticateCustomerHandler authenticateCustomerHandler;
    private final ListAllCustomersHandler listAllCustomersHandler;
    
    public CustomerController(
            CreateCustomerHandler createCustomerHandler,
            UpdateCustomerHandler updateCustomerHandler,
            DeleteCustomerHandler deleteCustomerHandler,
            GetCustomerByIdHandler getCustomerByIdHandler,
            GetCustomerByEmailHandler getCustomerByEmailHandler,
            AuthenticateCustomerHandler authenticateCustomerHandler,
            ListAllCustomersHandler listAllCustomersHandler) {
        this.createCustomerHandler = createCustomerHandler;
        this.updateCustomerHandler = updateCustomerHandler;
        this.deleteCustomerHandler = deleteCustomerHandler;
        this.getCustomerByIdHandler = getCustomerByIdHandler;
        this.getCustomerByEmailHandler = getCustomerByEmailHandler;
        this.authenticateCustomerHandler = authenticateCustomerHandler;
        this.listAllCustomersHandler = listAllCustomersHandler;
    }
    
    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided information")
    @ApiResponse(responseCode = "201", description = "Customer created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CreateCustomerCommand command = new CreateCustomerCommand(
            request.name(),
            request.email(),
            request.address(),
            request.password()
        );
        UUID customerId = createCustomerHandler.handle(command);
        
        // Fetch the created customer to return full response
        GetCustomerByIdQuery query = new GetCustomerByIdQuery(customerId);
        var customerDto = getCustomerByIdHandler.handle(query);
        
        CustomerResponse response = toResponse(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a customer", description = "Updates an existing customer")
    @ApiResponse(responseCode = "200", description = "Customer updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            id,
            request.name(),
            request.email(),
            request.address()
        );
        updateCustomerHandler.handle(command);
        
        // Fetch the updated customer
        GetCustomerByIdQuery query = new GetCustomerByIdQuery(id);
        var customerDto = getCustomerByIdHandler.handle(query);
        
        CustomerResponse response = toResponse(customerDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer", description = "Deletes a customer by ID")
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully")
    @ApiResponse(responseCode = "400", description = "Cannot delete customer with invoices")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID id) {
        DeleteCustomerCommand command = new DeleteCustomerCommand(id);
        deleteCustomerHandler.handle(command);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate customer", description = "Authenticates a customer by email and password")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
    public ResponseEntity<CustomerResponse> authenticateCustomer(
            @Valid @RequestBody AuthenticateCustomerRequest request) {
        AuthenticateCustomerCommand command = new AuthenticateCustomerCommand(
            request.email(),
            request.password()
        );
        var customerDto = authenticateCustomerHandler.handle(command);
        
        CustomerResponse response = toResponse(customerDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search/by-email")
    @Operation(summary = "Get customer by email", description = "Retrieves a customer by their email address")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(
            @Parameter(description = "Customer email") @RequestParam String email) {
        GetCustomerByEmailQuery query = new GetCustomerByEmailQuery(email);
        var customerDto = getCustomerByEmailHandler.handle(query);
        
        CustomerResponse response = toResponse(customerDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their ID")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable UUID id) {
        GetCustomerByIdQuery query = new GetCustomerByIdQuery(id);
        var customerDto = getCustomerByIdHandler.handle(query);
        
        CustomerResponse response = toResponse(customerDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "List all customers", description = "Retrieves a paginated list of all customers")
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    public ResponseEntity<PagedResponse<CustomerResponse>> listAllCustomers(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy) {
        ListAllCustomersQuery query = new ListAllCustomersQuery(page, size, sortBy);
        PagedResult<com.invoiceme.application.customers.getById.CustomerDto> result = listAllCustomersHandler.handle(query);
        
        PagedResponse<CustomerResponse> response = PagedResponse.of(
            result.content().stream()
                    .map(this::toResponse)
                    .toList(),
            result.page(),
            result.size(),
            result.totalElements()
        );
        return ResponseEntity.ok(response);
    }
    
    private CustomerResponse toResponse(com.invoiceme.application.customers.getById.CustomerDto dto) {
        return new CustomerResponse(
            dto.id(),
            dto.name(),
            dto.email(),
            dto.address(),
            dto.createdAt(),
            dto.updatedAt()
        );
    }
}

