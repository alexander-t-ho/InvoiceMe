package com.invoiceme.application.customers.listAll;

import com.invoiceme.application.customers.getById.CustomerDto;
import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for ListAllCustomersQuery.
 * Retrieves all customers with pagination.
 */
@Service
public class ListAllCustomersHandler {
    
    private final CustomerRepository customerRepository;
    
    public ListAllCustomersHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Transactional(readOnly = true)
    public PagedResult<CustomerDto> handle(ListAllCustomersQuery query) {
        // Get paginated customers
        List<Customer> customers = customerRepository.findAll(
            query.page(),
            query.size(),
            query.sortBy()
        );
        
        // Get total count
        long totalElements = customerRepository.count();
        
        // Convert to DTOs
        List<CustomerDto> customerDtos = customers.stream()
                .map(this::toDto)
                .toList();
        
        return PagedResult.of(
            customerDtos,
            query.page(),
            query.size(),
            totalElements
        );
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

