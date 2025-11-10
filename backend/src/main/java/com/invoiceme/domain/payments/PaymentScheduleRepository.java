package com.invoiceme.domain.payments;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PaymentSchedule aggregate.
 * Defined in domain layer to maintain dependency inversion.
 */
public interface PaymentScheduleRepository {
    
    /**
     * Saves a payment schedule.
     * @param schedule The payment schedule to save
     * @return The saved payment schedule
     */
    PaymentSchedule save(PaymentSchedule schedule);
    
    /**
     * Saves multiple payment schedules.
     * @param schedules The payment schedules to save
     */
    void saveAll(List<PaymentSchedule> schedules);
    
    /**
     * Finds a payment schedule by ID.
     * @param id The schedule ID
     * @return Optional containing the schedule if found
     */
    Optional<PaymentSchedule> findById(UUID id);
    
    /**
     * Finds all payment schedules for an invoice.
     * @param invoiceId The invoice ID
     * @return List of payment schedules ordered by installment number
     */
    List<PaymentSchedule> findByInvoiceId(UUID invoiceId);
    
    /**
     * Finds all upcoming installments (PENDING or OVERDUE) up to a certain date.
     * @param upToDate The date to check up to
     * @return List of upcoming installments
     */
    List<PaymentSchedule> findUpcomingInstallments(LocalDate upToDate);
    
    /**
     * Deletes all payment schedules for an invoice.
     * @param invoiceId The invoice ID
     */
    void deleteByInvoiceId(UUID invoiceId);
}



