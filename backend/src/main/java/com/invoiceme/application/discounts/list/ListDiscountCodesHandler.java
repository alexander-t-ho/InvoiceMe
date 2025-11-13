package com.invoiceme.application.discounts.list;

import com.invoiceme.domain.discounts.DiscountCode;
import com.invoiceme.domain.discounts.DiscountCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for ListDiscountCodesQuery.
 * Lists all discount codes.
 */
@Service
public class ListDiscountCodesHandler {
    
    private final DiscountCodeRepository discountCodeRepository;
    
    public ListDiscountCodesHandler(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }
    
    @Transactional(readOnly = true)
    public List<DiscountCodeDto> handle(ListDiscountCodesQuery query) {
        List<DiscountCode> codes = discountCodeRepository.findAll();
        return codes.stream()
                .map(code -> new DiscountCodeDto(
                    code.getCode(),
                    code.getDiscountPercent(),
                    code.isActive(),
                    code.getCreatedAt(),
                    code.getUpdatedAt()
                ))
                .toList();
    }
    
    public record DiscountCodeDto(
        String code,
        java.math.BigDecimal discountPercent,
        boolean isActive,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt
    ) {
    }
}









