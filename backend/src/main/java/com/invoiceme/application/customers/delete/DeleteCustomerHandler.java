package com.invoiceme.application.customers.delete;

import com.invoiceme.domain.customers.CustomerRepository;
import com.invoiceme.domain.exceptions.DomainValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for DeleteCustomerCommand.
 * Deletes a customer if they have no invoices.
 */
@Service
public class DeleteCustomerHandler {
    
    private final CustomerRepository customerRepository;
    
    public DeleteCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Transactional
    public void handle(DeleteCustomerCommand command) {
        // Check if customer exists
        if (!customerRepository.existsById(command.customerId())) {
            throw new DomainValidationException(
                "Customer with ID " + command.customerId() + " not found"
            );
        }
        
        // Business rule: Cannot delete customer if they have invoices
        if (customerRepository.hasInvoices(command.customerId())) {
            throw new DomainValidationException(
                "Cannot delete customer with ID " + command.customerId() + 
                " because they have invoices"
            );
        }
        
        // Delete customer
        customerRepository.deleteById(command.customerId());
    }
}


