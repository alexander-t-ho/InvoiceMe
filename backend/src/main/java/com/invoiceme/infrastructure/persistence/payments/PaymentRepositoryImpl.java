package com.invoiceme.infrastructure.persistence.payments;

import com.invoiceme.domain.payments.Payment;
import com.invoiceme.domain.payments.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PaymentRepository using Spring Data JPA.
 * Bridges domain repository interface with JPA implementation.
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    
    private final PaymentJpaRepository jpaRepository;
    
    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.fromDomain(payment);
        PaymentEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(PaymentEntity::toDomain);
    }
    
    @Override
    public List<Payment> findByInvoiceId(UUID invoiceId) {
        return jpaRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(PaymentEntity::toDomain)
                .collect(Collectors.toList());
    }
}


