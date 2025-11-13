package com.invoiceme.application.discounts.remove;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for RemoveDiscountCodeCommand.
 * Removes a discount code from an invoice.
 */
@Service
public class RemoveDiscountCodeHandler {
    
    private final InvoiceRepository invoiceRepository;
    
    public RemoveDiscountCodeHandler(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
    
    @Transactional
    public void handle(RemoveDiscountCodeCommand command) {
        // Load invoice
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + command.invoiceId() + " not found"
                ));
        
        // Remove discount (domain method validates state)
        invoice.removeDiscount();
        
        // Save invoice
        invoiceRepository.save(invoice);
    }
}









