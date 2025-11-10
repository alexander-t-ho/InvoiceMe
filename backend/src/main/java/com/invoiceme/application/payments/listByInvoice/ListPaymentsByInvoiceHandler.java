package com.invoiceme.application.payments.listByInvoice;

import com.invoiceme.application.invoices.getById.PaymentDto;
import com.invoiceme.domain.payments.Payment;
import com.invoiceme.domain.payments.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for ListPaymentsByInvoiceQuery.
 * Retrieves all payments for an invoice.
 */
@Service
public class ListPaymentsByInvoiceHandler {
    
    private final PaymentRepository paymentRepository;
    
    public ListPaymentsByInvoiceHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDto> handle(ListPaymentsByInvoiceQuery query) {
        List<Payment> payments = paymentRepository.findByInvoiceId(query.invoiceId());
        
        return payments.stream()
                .map(this::toDto)
                .toList();
    }
    
    private PaymentDto toDto(Payment payment) {
        return new PaymentDto(
            payment.getId(),
            payment.getAmount(),
            payment.getPaymentDate(),
            payment.getPaymentMethod(),
            payment.getCreatedAt()
        );
    }
}

