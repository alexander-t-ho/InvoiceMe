package com.invoiceme.application.invoices.listByStatus;

import com.invoiceme.application.customers.listAll.PagedResult;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for ListInvoicesByStatusQuery.
 * Retrieves invoices filtered by status with pagination.
 */
@Service
public class ListInvoicesByStatusHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    
    public ListInvoicesByStatusHandler(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }
    
    @Transactional(readOnly = true)
    public PagedResult<InvoiceSummaryDto> handle(ListInvoicesByStatusQuery query) {
        // Get invoices by status (will be implemented in repository)
        List<Invoice> invoices = invoiceRepository.findByStatus(
            query.status(),
            query.page(),
            query.size()
        );
        
        // Get total count
        long totalElements = invoiceRepository.countByStatus(query.status());
        
        // Convert to DTOs
        List<InvoiceSummaryDto> invoiceDtos = invoices.stream()
                .map(invoice -> {
                    String customerName = customerRepository.findById(invoice.getCustomerId())
                            .map(customer -> customer.getName())
                            .orElse("Unknown Customer");
                    return toSummaryDto(invoice, customerName);
                })
                .toList();
        
        return PagedResult.of(
            invoiceDtos,
            query.page(),
            query.size(),
            totalElements
        );
    }
    
    private InvoiceSummaryDto toSummaryDto(Invoice invoice, String customerName) {
        return new InvoiceSummaryDto(
            invoice.getId(),
            invoice.getCustomerId(),
            customerName,
            invoice.getStatus(),
            invoice.getIssueDate(),
            invoice.getDueDate(),
            invoice.calculateTotal(),
            invoice.calculateBalance()
        );
    }
}

