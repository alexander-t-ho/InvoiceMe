package com.invoiceme.application.invoices.update;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.exceptions.InvalidInvoiceStateException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for UpdateInvoiceCommand.
 * Updates invoice details (only allowed for DRAFT invoices).
 */
@Service
public class UpdateInvoiceHandler {
    
    private final InvoiceRepository invoiceRepository;
    
    public UpdateInvoiceHandler(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
    
    @Transactional
    public void handle(UpdateInvoiceCommand command) {
        // Load invoice
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + command.invoiceId() + " not found"
                ));
        
        // Update invoice dates (domain method validates state)
        invoice.updateDates(command.issueDate(), command.dueDate());
        
        // Save invoice
        invoiceRepository.save(invoice);
    }
}

