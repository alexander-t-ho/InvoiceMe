package com.invoiceme.domain.payments;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Payment aggregate.
 * Defined in domain layer to maintain dependency inversion.
 */
public interface PaymentRepository {
    
    /**
     * Saves a payment.
     * @param payment The payment to save
     * @return The saved payment
     */
    Payment save(Payment payment);
    
    /**
     * Finds a payment by ID.
     * @param id The payment ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findById(UUID id);
    
    /**
     * Finds all payments for an invoice.
     * @param invoiceId The invoice ID
     * @return List of payments
     */
    List<Payment> findByInvoiceId(UUID invoiceId);
}


