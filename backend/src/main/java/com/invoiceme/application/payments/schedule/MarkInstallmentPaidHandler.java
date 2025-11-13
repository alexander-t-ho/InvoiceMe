package com.invoiceme.application.payments.schedule;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.payments.PaymentSchedule;
import com.invoiceme.domain.payments.PaymentScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Handler for MarkInstallmentPaidCommand.
 * Marks the next pending installment as paid when a payment is recorded.
 */
@Service
public class MarkInstallmentPaidHandler {
    
    private final PaymentScheduleRepository paymentScheduleRepository;
    
    public MarkInstallmentPaidHandler(PaymentScheduleRepository paymentScheduleRepository) {
        this.paymentScheduleRepository = paymentScheduleRepository;
    }
    
    @Transactional
    public void handle(MarkInstallmentPaidCommand command) {
        List<PaymentSchedule> schedules = paymentScheduleRepository.findByInvoiceId(command.invoiceId());
        
        if (schedules.isEmpty()) {
            return; // No payment schedule for this invoice
        }
        
        // Find the next pending installment
        PaymentSchedule nextPending = schedules.stream()
                .filter(s -> s.getStatus() == PaymentSchedule.InstallmentStatus.PENDING)
                .min(Comparator.comparing(PaymentSchedule::getInstallmentNumber))
                .orElse(null);
        
        if (nextPending == null) {
            return; // All installments are already paid
        }
        
        // Normalize both amounts to 2 decimal places for comparison
        // This ensures consistent comparison even if scales differ
        BigDecimal paymentAmount = command.paymentAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal installmentAmount = nextPending.getAmount().setScale(2, RoundingMode.HALF_UP);
        
        // Check if payment amount matches (with small tolerance for rounding)
        BigDecimal difference = paymentAmount.subtract(installmentAmount).abs();
        if (difference.compareTo(new BigDecimal("0.01")) > 0) {
            // Payment doesn't match installment amount - don't auto-mark as paid
            // This allows for partial payments or overpayments
            return;
        }
        
        // Mark installment as paid
        nextPending.markAsPaid();
        paymentScheduleRepository.save(nextPending);
    }
}







