package com.invoiceme.application.invoices.listByCustomer;

import com.invoiceme.application.customers.listAll.PagedResult;
import com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto;
import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for ListInvoicesByCustomerQuery.
 * Retrieves invoices filtered by customer with pagination.
 */
@Service
public class ListInvoicesByCustomerHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    
    public ListInvoicesByCustomerHandler(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }
    
    @Transactional(readOnly = true)
    public PagedResult<InvoiceSummaryDto> handle(ListInvoicesByCustomerQuery query) {
        // Get invoices by customer
        List<Invoice> invoices = invoiceRepository.findByCustomerId(
            query.customerId(),
            query.page(),
            query.size()
        );
        
        // Get total count
        long totalElements = invoiceRepository.countByCustomerId(query.customerId());
        
        // Get customer name
        String customerName = customerRepository.findById(query.customerId())
                .map(customer -> customer.getName())
                .orElse("Unknown Customer");
        
        // Convert to DTOs
        List<InvoiceSummaryDto> invoiceDtos = invoices.stream()
                .map(invoice -> toSummaryDto(invoice, customerName))
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

