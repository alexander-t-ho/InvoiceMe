package com.invoiceme.application.discounts.apply;

import com.invoiceme.domain.discounts.DiscountCode;
import com.invoiceme.domain.discounts.DiscountCodeRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for ApplyDiscountCodeCommand.
 * Validates and applies a discount code to an invoice.
 */
@Service
public class ApplyDiscountCodeHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final DiscountCodeRepository discountCodeRepository;
    
    public ApplyDiscountCodeHandler(
            InvoiceRepository invoiceRepository,
            DiscountCodeRepository discountCodeRepository) {
        this.invoiceRepository = invoiceRepository;
        this.discountCodeRepository = discountCodeRepository;
    }
    
    @Transactional
    public void handle(ApplyDiscountCodeCommand command) {
        // Load invoice
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + command.invoiceId() + " not found"
                ));
        
        // Find discount code (case-insensitive)
        String codeUpper = command.discountCode().trim().toUpperCase();
        DiscountCode discountCode = discountCodeRepository.findByCode(codeUpper)
                .orElseThrow(() -> new DomainValidationException(
                    "Discount code '" + command.discountCode() + "' not found"
                ));
        
        // Validate discount code is active
        if (!discountCode.isActive()) {
            throw new DomainValidationException(
                "Discount code '" + command.discountCode() + "' is not active"
            );
        }
        
        // Apply discount to invoice (domain method validates state)
        invoice.applyDiscount(discountCode.getCode(), discountCode.getDiscountPercent());
        
        // Save invoice
        invoiceRepository.save(invoice);
    }
}



