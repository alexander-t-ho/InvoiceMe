package com.invoiceme.application.invoices.getById;

import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for GetInvoiceByIdQuery.
 * Retrieves an invoice by ID with all related data.
 */
@Service
public class GetInvoiceByIdHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    
    public GetInvoiceByIdHandler(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }
    
    @Transactional(readOnly = true)
    public InvoiceDto handle(GetInvoiceByIdQuery query) {
        Invoice invoice = invoiceRepository.findById(query.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + query.invoiceId() + " not found"
                ));
        
        // Get customer name
        String customerName = customerRepository.findById(invoice.getCustomerId())
                .map(customer -> customer.getName())
                .orElse("Unknown Customer");
        
        return toDto(invoice, customerName);
    }
    
    private InvoiceDto toDto(Invoice invoice, String customerName) {
        return new InvoiceDto(
            invoice.getId(),
            invoice.getCustomerId(),
            customerName,
            invoice.getStatus(),
            invoice.getIssueDate(),
            invoice.getDueDate(),
            invoice.getPaymentPlan(),
            invoice.getDiscountCode(),
            invoice.getDiscountAmount(),
            invoice.calculateSubtotal(),
            invoice.calculateTotal(),
            invoice.calculateBalance(),
            invoice.getLineItems().stream()
                    .map(item -> new LineItemDto(
                        item.getId(),
                        item.getDescription(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotal()
                    ))
                    .toList(),
            invoice.getPayments().stream()
                    .map(payment -> new PaymentDto(
                        payment.getId(),
                        payment.getAmount(),
                        payment.getPaymentDate(),
                        payment.getPaymentMethod(),
                        payment.getCreatedAt()
                    ))
                    .toList(),
            invoice.getCreatedAt(),
            invoice.getUpdatedAt()
        );
    }
}

