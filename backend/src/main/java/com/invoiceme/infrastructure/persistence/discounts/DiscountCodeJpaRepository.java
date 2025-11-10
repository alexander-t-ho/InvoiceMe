package com.invoiceme.infrastructure.persistence.discounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for DiscountCode entity.
 */
@Repository
interface DiscountCodeJpaRepository extends JpaRepository<DiscountCodeEntity, String> {
    
    Optional<DiscountCodeEntity> findByCodeIgnoreCase(String code);
    
    boolean existsByCodeIgnoreCase(String code);
}



