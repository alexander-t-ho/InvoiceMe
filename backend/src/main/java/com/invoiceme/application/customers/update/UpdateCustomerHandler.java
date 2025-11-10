package com.invoiceme.application.customers.update;

import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for UpdateCustomerCommand.
 * Updates an existing customer's details.
 */
@Service
public class UpdateCustomerHandler {
    
    private final CustomerRepository customerRepository;
    
    public UpdateCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Transactional
    public void handle(UpdateCustomerCommand command) {
        // Load customer
        Customer customer = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new DomainValidationException(
                    "Customer with ID " + command.customerId() + " not found"
                ));
        
        // Check if email is being changed and if new email already exists
        if (!customer.getEmail().equals(command.email())) {
            customerRepository.findByEmail(command.email())
                    .ifPresent(existing -> {
                        throw new DomainValidationException(
                            "Customer with email " + command.email() + " already exists"
                        );
                    });
        }
        
        // Update customer using domain method
        customer.updateDetails(
            command.name(),
            command.email(),
            command.address()
        );
        
        // Save customer
        customerRepository.save(customer);
    }
}


