package com.invoiceme.infrastructure.discounts;

import com.invoiceme.domain.discounts.DiscountCode;
import com.invoiceme.domain.discounts.DiscountCodeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Initializes default discount codes on application startup.
 */
@Component
public class DiscountCodeInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscountCodeInitializer.class);
    
    private final DiscountCodeRepository discountCodeRepository;
    
    public DiscountCodeInitializer(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }
    
    @PostConstruct
    public void initializeDiscountCodes() {
        // Initialize "Save15" - 15% discount
        if (!discountCodeRepository.existsByCode("SAVE15")) {
            DiscountCode save15 = DiscountCode.create("SAVE15", new BigDecimal("15"));
            discountCodeRepository.save(save15);
            logger.info("Initialized discount code: SAVE15 (15%)");
        }
        
        // Initialize "FandF" - 30% discount
        if (!discountCodeRepository.existsByCode("FANDF")) {
            DiscountCode fandf = DiscountCode.create("FANDF", new BigDecimal("30"));
            discountCodeRepository.save(fandf);
            logger.info("Initialized discount code: FANDF (30%)");
        }
    }
}



