package com.invoiceme.application.customers.getById;

import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for GetCustomerByIdQuery.
 * Retrieves a customer by ID.
 */
@Service
public class GetCustomerByIdHandler {
    
    private final CustomerRepository customerRepository;
    
    public GetCustomerByIdHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Transactional(readOnly = true)
    public CustomerDto handle(GetCustomerByIdQuery query) {
        Customer customer = customerRepository.findById(query.customerId())
                .orElseThrow(() -> new DomainValidationException(
                    "Customer with ID " + query.customerId() + " not found"
                ));
        
        return toDto(customer);
    }
    
    private CustomerDto toDto(Customer customer) {
        return new CustomerDto(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getAddress(),
            customer.getCreatedAt(),
            customer.getUpdatedAt()
        );
    }
}

