package com.invoiceme.application.invoices.addLineItem;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.invoices.LineItem;
import com.invoiceme.domain.items.Item;
import com.invoiceme.domain.items.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for AddLineItemCommand.
 * Adds a line item to an invoice (only allowed for DRAFT invoices).
 * If itemId is provided, description and unitPrice will be auto-filled from the item library.
 */
@Service
public class AddLineItemHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    
    public AddLineItemHandler(InvoiceRepository invoiceRepository, ItemRepository itemRepository) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
    }
    
    @Transactional
    public void handle(AddLineItemCommand command) {
        // Load invoice
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + command.invoiceId() + " not found"
                ));
        
        // If itemId is provided, load item and use its description and unitPrice
        String description = command.description();
        java.math.BigDecimal unitPrice = command.unitPrice();
        
        if (command.itemId() != null) {
            Item item = itemRepository.findById(command.itemId())
                    .orElseThrow(() -> new DomainValidationException(
                        "Item with ID " + command.itemId() + " not found"
                    ));
            description = item.getDescription();
            unitPrice = item.getUnitPrice();
        }
        
        // Create line item
        LineItem lineItem = LineItem.create(
            description,
            command.quantity(),
            unitPrice
        );
        
        // Add line item (domain method validates state)
        invoice.addLineItem(lineItem);
        
        // Save invoice
        invoiceRepository.save(invoice);
    }
}


