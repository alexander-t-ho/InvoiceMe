package com.invoiceme.application.invoices.create;

import com.invoiceme.application.payments.schedule.CreatePaymentScheduleCommand;
import com.invoiceme.application.payments.schedule.CreatePaymentScheduleHandler;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.payments.PaymentPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for CreateInvoiceCommand.
 * Creates a new invoice in DRAFT status.
 * If PAY_IN_4 is selected, creates payment schedule after invoice is sent.
 */
@Service
public class CreateInvoiceHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final CreatePaymentScheduleHandler createPaymentScheduleHandler;
    
    public CreateInvoiceHandler(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository,
            CreatePaymentScheduleHandler createPaymentScheduleHandler) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.createPaymentScheduleHandler = createPaymentScheduleHandler;
    }
    
    @Transactional
    public UUID handle(CreateInvoiceCommand command) {
        // Validate customer exists
        if (!customerRepository.existsById(command.customerId())) {
            throw new DomainValidationException(
                "Customer with ID " + command.customerId() + " not found"
            );
        }
        
        // Create invoice using domain factory method
        Invoice invoice = Invoice.create(
            command.customerId(),
            command.issueDate(),
            command.dueDate(),
            command.paymentPlan() != null ? command.paymentPlan() : PaymentPlan.FULL
        );
        
        // Save invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        return savedInvoice.getId();
    }
}


