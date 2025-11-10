package com.invoiceme.domain.customers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Customer aggregate.
 * Defined in domain layer to maintain dependency inversion.
 */
public interface CustomerRepository {
    
    /**
     * Saves a customer.
     * @param customer The customer to save
     * @return The saved customer
     */
    Customer save(Customer customer);
    
    /**
     * Finds a customer by ID.
     * @param id The customer ID
     * @return Optional containing the customer if found
     */
    Optional<Customer> findById(UUID id);
    
    /**
     * Finds a customer by email.
     * @param email The customer email
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Finds all customers with pagination.
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Sort field
     * @return List of customers
     */
    List<Customer> findAll(int page, int size, String sortBy);
    
    /**
     * Counts total number of customers.
     * @return Total count
     */
    long count();
    
    /**
     * Checks if a customer exists by ID.
     * @param id The customer ID
     * @return true if customer exists
     */
    boolean existsById(UUID id);
    
    /**
     * Deletes a customer by ID.
     * @param id The customer ID
     */
    void deleteById(UUID id);
    
    /**
     * Checks if customer has any invoices.
     * @param customerId The customer ID
     * @return true if customer has invoices
     */
    boolean hasInvoices(UUID customerId);
}


