package com.invoiceme.application.customers.getByEmail;

import com.invoiceme.application.customers.getById.CustomerDto;
import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for GetCustomerByEmailQuery.
 * Retrieves a customer by email.
 */
@Service
public class GetCustomerByEmailHandler {
    
    private final CustomerRepository customerRepository;
    
    public GetCustomerByEmailHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Transactional(readOnly = true)
    public CustomerDto handle(GetCustomerByEmailQuery query) {
        Customer customer = customerRepository.findByEmail(query.email())
                .orElseThrow(() -> new DomainValidationException(
                    "Customer with email " + query.email() + " not found"
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

