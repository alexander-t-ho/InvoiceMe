package com.invoiceme.domain.invoices;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Invoice aggregate.
 * Defined in domain layer to maintain dependency inversion.
 */
public interface InvoiceRepository {
    
    /**
     * Saves an invoice.
     * @param invoice The invoice to save
     * @return The saved invoice
     */
    Invoice save(Invoice invoice);
    
    /**
     * Finds an invoice by ID.
     * @param id The invoice ID
     * @return Optional containing the invoice if found
     */
    Optional<Invoice> findById(UUID id);
    
    /**
     * Finds invoices by status with pagination.
     * @param status The invoice status
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of invoices
     */
    List<Invoice> findByStatus(InvoiceStatus status, int page, int size);
    
    /**
     * Counts invoices by status.
     * @param status The invoice status
     * @return Total count
     */
    long countByStatus(InvoiceStatus status);
    
    /**
     * Finds invoices by customer ID with pagination.
     * @param customerId The customer ID
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of invoices
     */
    List<Invoice> findByCustomerId(UUID customerId, int page, int size);
    
    /**
     * Counts invoices by customer ID.
     * @param customerId The customer ID
     * @return Total count
     */
    long countByCustomerId(UUID customerId);
    
    /**
     * Finds all invoices with pagination (regardless of status).
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of invoices
     */
    List<Invoice> findAll(int page, int size);
    
    /**
     * Counts total number of invoices.
     * @return Total count
     */
    long count();
    
    /**
     * Checks if an invoice exists by ID.
     * @param id The invoice ID
     * @return true if invoice exists
     */
    boolean existsById(UUID id);
    
    /**
     * Deletes an invoice by ID.
     * @param id The invoice ID
     */
    void deleteById(UUID id);
}


