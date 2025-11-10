package com.invoiceme.application.customers.create;

import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for CreateCustomerCommand.
 * Creates a new customer in the system.
 */
@Service
public class CreateCustomerHandler {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CreateCustomerHandler(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public UUID handle(CreateCustomerCommand command) {
        // Check if email already exists
        customerRepository.findByEmail(command.email())
                .ifPresent(existing -> {
                    throw new DomainValidationException(
                        "Customer with email " + command.email() + " already exists"
                    );
                });
        
        // Always use "123456" as the default password for all new customers
        // This allows customers to log in immediately after creation
        String passwordHash = passwordEncoder.encode("123456");
        
        // Create customer using domain factory method
        Customer customer = Customer.create(
            command.name(),
            command.email(),
            command.address(),
            passwordHash
        );
        
        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        
        return savedCustomer.getId();
    }
}


