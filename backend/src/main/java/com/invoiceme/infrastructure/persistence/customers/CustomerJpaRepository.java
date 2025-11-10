package com.invoiceme.infrastructure.persistence.customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Customer entity.
 * This is the infrastructure implementation of CustomerRepository.
 */
@Repository
interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    
    Optional<CustomerEntity> findByEmail(String email);
    
    Page<CustomerEntity> findAll(Pageable pageable);
    
    @Query("SELECT COUNT(i) > 0 FROM InvoiceEntity i WHERE i.customerId = :customerId")
    boolean hasInvoices(@Param("customerId") UUID customerId);
}


