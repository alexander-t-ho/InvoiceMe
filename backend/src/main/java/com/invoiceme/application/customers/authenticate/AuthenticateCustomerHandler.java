package com.invoiceme.application.customers.authenticate;

import com.invoiceme.application.customers.getById.CustomerDto;
import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for AuthenticateCustomerCommand.
 * Authenticates a customer by email and password.
 */
@Service
public class AuthenticateCustomerHandler {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthenticateCustomerHandler(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public CustomerDto handle(AuthenticateCustomerCommand command) {
        // Find customer by email
        Customer customer = customerRepository.findByEmail(command.email())
                .orElseThrow(() -> new DomainValidationException(
                    "Invalid email or password"
                ));
        
        // Check if password hash exists (for existing customers without password)
        // If missing, set default password "123456" as a migration path
        if (customer.getPasswordHash() == null || customer.getPasswordHash().isEmpty()) {
            // Set default password for existing customers
            String defaultPasswordHash = passwordEncoder.encode("123456");
            customer.updatePassword(defaultPasswordHash);
            customerRepository.save(customer);
            
            // Now verify the provided password matches the default
            if (!passwordEncoder.matches(command.password(), defaultPasswordHash)) {
                throw new DomainValidationException(
                    "Invalid email or password"
                );
            }
        } else {
            // Verify password for customers with existing password
            if (!passwordEncoder.matches(command.password(), customer.getPasswordHash())) {
                throw new DomainValidationException(
                    "Invalid email or password"
                );
            }
        }
        
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

