package com.invoiceme.application.payments.schedule;

import com.invoiceme.domain.payments.PaymentSchedule;
import com.invoiceme.domain.payments.PaymentScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for CreatePaymentScheduleCommand.
 * Creates 4 payment installments every 2 weeks.
 */
@Service
public class CreatePaymentScheduleHandler {
    
    private final PaymentScheduleRepository paymentScheduleRepository;
    
    public CreatePaymentScheduleHandler(PaymentScheduleRepository paymentScheduleRepository) {
        this.paymentScheduleRepository = paymentScheduleRepository;
    }
    
    @Transactional
    public void handle(CreatePaymentScheduleCommand command) {
        // Calculate installment amount (divide total by 4, rounding to 2 decimal places)
        BigDecimal installmentAmount = command.totalAmount()
                .divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
        
        // Calculate remainder to adjust last installment
        BigDecimal totalOfFour = installmentAmount.multiply(new BigDecimal("4"));
        BigDecimal remainder = command.totalAmount().subtract(totalOfFour);
        
        List<PaymentSchedule> schedules = new ArrayList<>();
        LocalDate currentDate = command.startDate();
        
        for (int i = 1; i <= 4; i++) {
            BigDecimal amount = installmentAmount;
            // Add remainder to last installment to ensure total matches
            if (i == 4) {
                amount = amount.add(remainder);
            }
            
            PaymentSchedule schedule = PaymentSchedule.create(
                command.invoiceId(),
                i,
                amount,
                currentDate
            );
            
            schedules.add(schedule);
            
            // Next installment is 2 weeks (14 days) later
            currentDate = currentDate.plusWeeks(2);
        }
        
        // Save all schedules
        paymentScheduleRepository.saveAll(schedules);
    }
}



