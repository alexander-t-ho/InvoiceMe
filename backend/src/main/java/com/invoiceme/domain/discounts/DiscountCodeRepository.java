package com.invoiceme.domain.discounts;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DiscountCode aggregate.
 * Defined in domain layer to maintain dependency inversion.
 */
public interface DiscountCodeRepository {
    
    /**
     * Saves a discount code.
     * @param discountCode The discount code to save
     * @return The saved discount code
     */
    DiscountCode save(DiscountCode discountCode);
    
    /**
     * Finds a discount code by code (case-insensitive).
     * @param code The discount code
     * @return Optional containing the discount code if found
     */
    Optional<DiscountCode> findByCode(String code);
    
    /**
     * Finds all discount codes.
     * @return List of all discount codes
     */
    List<DiscountCode> findAll();
    
    /**
     * Checks if a discount code exists.
     * @param code The discount code
     * @return true if discount code exists
     */
    boolean existsByCode(String code);
    
    /**
     * Deletes a discount code by code.
     * @param code The discount code
     */
    void deleteByCode(String code);
}









