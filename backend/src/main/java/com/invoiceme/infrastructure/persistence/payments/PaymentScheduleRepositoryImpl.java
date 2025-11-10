package com.invoiceme.infrastructure.persistence.payments;

import com.invoiceme.domain.payments.PaymentSchedule;
import com.invoiceme.domain.payments.PaymentScheduleRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PaymentScheduleRepository using Spring Data JPA.
 * Bridges domain repository interface with JPA implementation.
 */
@Repository
public class PaymentScheduleRepositoryImpl implements PaymentScheduleRepository {
    
    private final PaymentScheduleJpaRepository jpaRepository;
    
    public PaymentScheduleRepositoryImpl(PaymentScheduleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public PaymentSchedule save(PaymentSchedule schedule) {
        PaymentScheduleEntity entity = PaymentScheduleEntity.fromDomain(schedule);
        PaymentScheduleEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public void saveAll(List<PaymentSchedule> schedules) {
        List<PaymentScheduleEntity> entities = schedules.stream()
                .map(PaymentScheduleEntity::fromDomain)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }
    
    @Override
    public Optional<PaymentSchedule> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(PaymentScheduleEntity::toDomain);
    }
    
    @Override
    public List<PaymentSchedule> findByInvoiceId(UUID invoiceId) {
        return jpaRepository.findByInvoiceIdOrderByInstallmentNumberAsc(invoiceId).stream()
                .map(PaymentScheduleEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PaymentSchedule> findUpcomingInstallments(LocalDate upToDate) {
        return jpaRepository.findUpcomingInstallments(upToDate).stream()
                .map(PaymentScheduleEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteByInvoiceId(UUID invoiceId) {
        jpaRepository.deleteByInvoiceId(invoiceId);
    }
}



