package com.invoiceme.infrastructure.persistence.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for Payment entity.
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    
    List<PaymentEntity> findByInvoiceId(UUID invoiceId);
}

