package com.invoiceme.infrastructure.persistence.invoices;

import com.invoiceme.domain.invoices.Invoice;
import com.invoiceme.domain.invoices.InvoiceRepository;
import com.invoiceme.domain.invoices.InvoiceStatus;
import com.invoiceme.domain.payments.Payment;
import com.invoiceme.infrastructure.persistence.payments.PaymentEntity;
import com.invoiceme.infrastructure.persistence.payments.PaymentJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of InvoiceRepository using Spring Data JPA.
 * Bridges domain repository interface with JPA implementation.
 */
@Repository
public class InvoiceRepositoryImpl implements InvoiceRepository {
    
    private final InvoiceJpaRepository jpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;
    
    public InvoiceRepositoryImpl(
            InvoiceJpaRepository jpaRepository,
            PaymentJpaRepository paymentJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.paymentJpaRepository = paymentJpaRepository;
    }
    
    @Override
    public Invoice save(Invoice invoice) {
        InvoiceEntity entity = InvoiceEntity.fromDomain(invoice);
        InvoiceEntity saved = jpaRepository.save(entity);
        
        // Load payments for this invoice
        List<Payment> payments = paymentJpaRepository.findByInvoiceId(saved.getId())
                .stream()
                .map(PaymentEntity::toDomain)
                .collect(Collectors.toList());
        
        return saved.toDomain(payments);
    }
    
    @Override
    public Optional<Invoice> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(entity -> {
                    // Load payments for this invoice
                    List<Payment> payments = paymentJpaRepository.findByInvoiceId(id)
                            .stream()
                            .map(PaymentEntity::toDomain)
                            .collect(Collectors.toList());
                    return entity.toDomain(payments);
                });
    }
    
    @Override
    public List<Invoice> findByStatus(InvoiceStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InvoiceEntity> invoicePage = jpaRepository.findByStatus(status, pageable);
        
        return invoicePage.getContent().stream()
                .map(entity -> {
                    List<Payment> payments = paymentJpaRepository.findByInvoiceId(entity.getId())
                            .stream()
                            .map(PaymentEntity::toDomain)
                            .collect(Collectors.toList());
                    return entity.toDomain(payments);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByStatus(InvoiceStatus status) {
        return jpaRepository.countByStatus(status);
    }
    
    @Override
    public List<Invoice> findByCustomerId(UUID customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InvoiceEntity> invoicePage = jpaRepository.findByCustomerId(customerId, pageable);
        
        return invoicePage.getContent().stream()
                .map(entity -> {
                    List<Payment> payments = paymentJpaRepository.findByInvoiceId(entity.getId())
                            .stream()
                            .map(PaymentEntity::toDomain)
                            .collect(Collectors.toList());
                    return entity.toDomain(payments);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByCustomerId(UUID customerId) {
        return jpaRepository.countByCustomerId(customerId);
    }
    
    @Override
    public List<Invoice> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InvoiceEntity> invoicePage = jpaRepository.findAll(pageable);
        
        return invoicePage.getContent().stream()
                .map(entity -> {
                    List<Payment> payments = paymentJpaRepository.findByInvoiceId(entity.getId())
                            .stream()
                            .map(PaymentEntity::toDomain)
                            .collect(Collectors.toList());
                    return entity.toDomain(payments);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

