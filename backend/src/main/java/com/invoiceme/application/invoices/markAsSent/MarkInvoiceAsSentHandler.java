package com.invoiceme.application.invoices.markAsSent;

import com.invoiceme.application.payments.schedule.CreatePaymentScheduleCommand;
import com.invoiceme.application.payments.schedule.CreatePaymentScheduleHandler;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.payments.PaymentPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Handler for MarkInvoiceAsSentCommand.
 * Marks an invoice as SENT (validates it has line items).
 * If PAY_IN_4 is selected, creates payment schedule.
 */
@Service
public class MarkInvoiceAsSentHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final CreatePaymentScheduleHandler createPaymentScheduleHandler;
    
    public MarkInvoiceAsSentHandler(
            InvoiceRepository invoiceRepository,
            CreatePaymentScheduleHandler createPaymentScheduleHandler) {
        this.invoiceRepository = invoiceRepository;
        this.createPaymentScheduleHandler = createPaymentScheduleHandler;
    }
    
    @Transactional
    public void handle(MarkInvoiceAsSentCommand command) {
        // Load invoice
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + command.invoiceId() + " not found"
                ));
        
        // Mark as sent (domain method validates state and line items)
        invoice.markAsSent();
        
        // Save invoice
        invoiceRepository.save(invoice);
        
        // If PAY_IN_4 is selected, create payment schedule
        if (invoice.getPaymentPlan() == PaymentPlan.PAY_IN_4) {
            // Start date is 2 weeks from issue date (first installment)
            LocalDate startDate = invoice.getIssueDate().plusWeeks(2);
            
            CreatePaymentScheduleCommand scheduleCommand = new CreatePaymentScheduleCommand(
                invoice.getId(),
                invoice.calculateTotal(),
                startDate
            );
            createPaymentScheduleHandler.handle(scheduleCommand);
        }
    }
}


