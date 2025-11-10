package com.invoiceme.infrastructure.persistence.invoices;

import com.invoiceme.domain.invoices.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for Invoice entity.
 */
@Repository
interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID> {
    
    Page<InvoiceEntity> findByStatus(InvoiceStatus status, Pageable pageable);
    
    long countByStatus(InvoiceStatus status);
    
    Page<InvoiceEntity> findByCustomerId(UUID customerId, Pageable pageable);
    
    long countByCustomerId(UUID customerId);
    
    Page<InvoiceEntity> findAll(Pageable pageable);
    
    long count();
}


