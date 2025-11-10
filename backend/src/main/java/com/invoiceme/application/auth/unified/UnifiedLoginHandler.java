package com.invoiceme.application.auth.unified;

import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.users.User;
import com.invoiceme.domain.users.UserRepository;
import com.invoiceme.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Handler for unified login that supports both admin users and customers.
 */
@Service
public class UnifiedLoginHandler {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    
    public UnifiedLoginHandler(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public UnifiedLoginResult handle(UnifiedLoginCommand command) {
        // Try to find as admin user (by username or email)
        Optional<User> userOpt = userRepository.findByUsername(command.identifier())
            .or(() -> userRepository.findByEmail(command.identifier()));
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPasswordHash() != null && user.validatePassword(command.password())) {
                String token = jwtService.generateToken(user.getUsername());
                return new UnifiedLoginResult(
                    "ADMIN",
                    token,
                    user.getId().toString(),
                    user.getUsername(),
                    user.getEmail(),
                    null // customerId
                );
            }
        }
        
        // Try to find as customer (by email)
        Optional<Customer> customerOpt = customerRepository.findByEmail(command.identifier());
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            
            // Handle missing password (migration)
            if (customer.getPasswordHash() == null || customer.getPasswordHash().isEmpty()) {
                String defaultPasswordHash = passwordEncoder.encode("123456");
                customer.updatePassword(defaultPasswordHash);
                customerRepository.save(customer);
            }
            
            if (passwordEncoder.matches(command.password(), customer.getPasswordHash())) {
                // Generate JWT token for customer (using customer ID as subject)
                String token = jwtService.generateToken(customer.getId().toString());
                return new UnifiedLoginResult(
                    "CUSTOMER",
                    token,
                    null, // userId
                    customer.getName(),
                    customer.getEmail(),
                    customer.getId().toString()
                );
            }
        }
        
        throw new DomainValidationException("Invalid username/email or password");
    }
    
    public record UnifiedLoginResult(
        String userType, // "ADMIN" or "CUSTOMER"
        String token,
        String userId, // null for customers
        String name, // username for admin, name for customer
        String email,
        String customerId // null for admins
    ) {
    }
}

