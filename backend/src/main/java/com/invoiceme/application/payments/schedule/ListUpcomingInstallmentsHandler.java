package com.invoiceme.application.payments.schedule;

import com.invoiceme.domain.payments.PaymentSchedule;
import com.invoiceme.domain.payments.PaymentScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Handler for ListUpcomingInstallmentsQuery.
 * Lists upcoming installments up to a certain date.
 */
@Service
public class ListUpcomingInstallmentsHandler {
    
    private final PaymentScheduleRepository paymentScheduleRepository;
    
    public ListUpcomingInstallmentsHandler(PaymentScheduleRepository paymentScheduleRepository) {
        this.paymentScheduleRepository = paymentScheduleRepository;
    }
    
    @Transactional(readOnly = true)
    public List<PaymentScheduleDto> handle(ListUpcomingInstallmentsQuery query) {
        List<PaymentSchedule> schedules = paymentScheduleRepository.findUpcomingInstallments(query.upToDate());
        
        return schedules.stream()
                .map(schedule -> new PaymentScheduleDto(
                    schedule.getId(),
                    schedule.getInvoiceId(),
                    schedule.getInstallmentNumber(),
                    schedule.getAmount(),
                    schedule.getDueDate(),
                    schedule.getStatus().name(),
                    schedule.getCreatedAt()
                ))
                .toList();
    }
    
    public record PaymentScheduleDto(
        UUID id,
        UUID invoiceId,
        int installmentNumber,
        java.math.BigDecimal amount,
        java.time.LocalDate dueDate,
        String status,
        java.time.LocalDateTime createdAt
    ) {
    }
}


