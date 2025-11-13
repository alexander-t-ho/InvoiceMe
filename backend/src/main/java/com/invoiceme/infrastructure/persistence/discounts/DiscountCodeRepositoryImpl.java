package com.invoiceme.infrastructure.persistence.discounts;

import com.invoiceme.domain.discounts.DiscountCode;
import com.invoiceme.domain.discounts.DiscountCodeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of DiscountCodeRepository using Spring Data JPA.
 * Bridges domain repository interface with JPA implementation.
 */
@Repository
public class DiscountCodeRepositoryImpl implements DiscountCodeRepository {
    
    private final DiscountCodeJpaRepository jpaRepository;
    
    public DiscountCodeRepositoryImpl(DiscountCodeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public DiscountCode save(DiscountCode discountCode) {
        DiscountCodeEntity entity = DiscountCodeEntity.fromDomain(discountCode);
        DiscountCodeEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<DiscountCode> findByCode(String code) {
        return jpaRepository.findByCodeIgnoreCase(code)
                .map(DiscountCodeEntity::toDomain);
    }
    
    @Override
    public List<DiscountCode> findAll() {
        return jpaRepository.findAll().stream()
                .map(DiscountCodeEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCodeIgnoreCase(code);
    }
    
    @Override
    public void deleteByCode(String code) {
        jpaRepository.deleteById(code.toUpperCase());
    }
}









