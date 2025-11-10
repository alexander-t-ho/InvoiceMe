package com.invoiceme.application.invoices.removeLineItem;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for RemoveLineItemCommand.
 * Removes a line item from an invoice (only allowed for DRAFT invoices).
 */
@Service
public class RemoveLineItemHandler {
    
    private final InvoiceRepository invoiceRepository;
    
    public RemoveLineItemHandler(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
    
    @Transactional
    public void handle(RemoveLineItemCommand command) {
        // Load invoice
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + command.invoiceId() + " not found"
                ));
        
        // Remove line item (domain method validates state)
        invoice.removeLineItem(command.lineItemId());
        
        // Save invoice
        invoiceRepository.save(invoice);
    }
}


