package com.invoiceme.application.discounts.validate;

import com.invoiceme.domain.discounts.DiscountCode;
import com.invoiceme.domain.discounts.DiscountCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for ValidateDiscountCodeQuery.
 * Validates if a discount code exists and is active.
 */
@Service
public class ValidateDiscountCodeHandler {
    
    private final DiscountCodeRepository discountCodeRepository;
    
    public ValidateDiscountCodeHandler(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }
    
    @Transactional(readOnly = true)
    public DiscountCodeValidationResult handle(ValidateDiscountCodeQuery query) {
        String codeUpper = query.code().trim().toUpperCase();
        var discountCodeOpt = discountCodeRepository.findByCode(codeUpper);
        
        if (discountCodeOpt.isEmpty()) {
            return new DiscountCodeValidationResult(false, "Discount code not found", null);
        }
        
        DiscountCode discountCode = discountCodeOpt.get();
        if (!discountCode.isActive()) {
            return new DiscountCodeValidationResult(false, "Discount code is not active", null);
        }
        
        return new DiscountCodeValidationResult(
            true,
            "Discount code is valid",
            discountCode.getDiscountPercent()
        );
    }
    
    public record DiscountCodeValidationResult(
        boolean isValid,
        String message,
        java.math.BigDecimal discountPercent
    ) {
    }
}









