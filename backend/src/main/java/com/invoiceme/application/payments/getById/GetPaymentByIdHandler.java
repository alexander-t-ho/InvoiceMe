package com.invoiceme.application.payments.getById;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.payments.Payment;
import com.invoiceme.domain.payments.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for GetPaymentByIdQuery.
 * Retrieves a payment by ID with invoice information.
 */
@Service
public class GetPaymentByIdHandler {
    
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    
    public GetPaymentByIdHandler(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }
    
    @Transactional(readOnly = true)
    public PaymentDetailDto handle(GetPaymentByIdQuery query) {
        Payment payment = paymentRepository.findById(query.paymentId())
                .orElseThrow(() -> new DomainValidationException(
                    "Payment with ID " + query.paymentId() + " not found"
                ));
        
        // Get invoice for invoice number (using invoice ID as number for now)
        String invoiceNumber = invoiceRepository.findById(payment.getInvoiceId())
                .map(invoice -> invoice.getId().toString())
                .orElse("Unknown");
        
        return toDto(payment, invoiceNumber);
    }
    
    private PaymentDetailDto toDto(Payment payment, String invoiceNumber) {
        return new PaymentDetailDto(
            payment.getId(),
            payment.getInvoiceId(),
            invoiceNumber,
            payment.getAmount(),
            payment.getPaymentDate(),
            payment.getPaymentMethod(),
            payment.getCreatedAt()
        );
    }
}

