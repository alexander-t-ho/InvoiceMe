package com.invoiceme.application.invoices.listAll;

import com.invoiceme.application.customers.listAll.PagedResult;
import com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for ListAllInvoicesQuery.
 * Retrieves all invoices regardless of status with pagination.
 */
@Service
public class ListAllInvoicesHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    
    public ListAllInvoicesHandler(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }
    
    @Transactional(readOnly = true)
    public PagedResult<InvoiceSummaryDto> handle(ListAllInvoicesQuery query) {
        // Get all invoices
        List<Invoice> invoices = invoiceRepository.findAll(
            query.page(),
            query.size()
        );
        
        // Get total count
        long totalElements = invoiceRepository.count();
        
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

