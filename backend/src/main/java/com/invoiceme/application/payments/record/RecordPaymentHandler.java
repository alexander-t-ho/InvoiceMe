package com.invoiceme.application.payments.record;

import com.invoiceme.application.payments.schedule.MarkInstallmentPaidCommand;
import com.invoiceme.application.payments.schedule.MarkInstallmentPaidHandler;
import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.exceptions.InvalidInvoiceStateException;
import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.payments.Payment;
import com.invoiceme.domain.payments.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for RecordPaymentCommand.
 * Records a payment and applies it to an invoice.
 * If invoice has PAY_IN_4, marks corresponding installment as paid.
 */
@Service
public class RecordPaymentHandler {
    
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final MarkInstallmentPaidHandler markInstallmentPaidHandler;
    
    public RecordPaymentHandler(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            MarkInstallmentPaidHandler markInstallmentPaidHandler) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.markInstallmentPaidHandler = markInstallmentPaidHandler;
    }
    
    @Transactional
    public UUID handle(RecordPaymentCommand command) {
        // Load invoice
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new DomainValidationException(
                    "Invoice with ID " + command.invoiceId() + " not found"
                ));
        
        // Validate invoice status (must be SENT or PAID to accept payments)
        if (invoice.getStatus() == com.invoiceme.domain.invoices.InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                "Cannot record payment for invoice in DRAFT status. Invoice must be SENT first."
            );
        }
        
        // Create payment
        Payment payment = Payment.create(
            command.invoiceId(),
            command.amount(),
            command.paymentDate(),
            command.paymentMethod()
        );
        
        // Validate payment against invoice (domain method)
        payment.validateAgainstInvoice(invoice);
        
        // Apply payment to invoice (domain method handles balance and status transition)
        invoice.applyPayment(payment);
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // Save invoice (balance and status may have changed)
        invoiceRepository.save(invoice);
        
        // If invoice has PAY_IN_4, mark corresponding installment as paid
        if (invoice.getPaymentPlan() == com.invoiceme.domain.payments.PaymentPlan.PAY_IN_4) {
            MarkInstallmentPaidCommand markCommand = new MarkInstallmentPaidCommand(
                command.invoiceId(),
                command.amount()
            );
            markInstallmentPaidHandler.handle(markCommand);
        }
        
        return savedPayment.getId();
    }
}


