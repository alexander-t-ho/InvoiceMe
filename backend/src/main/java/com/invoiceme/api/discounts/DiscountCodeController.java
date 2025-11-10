package com.invoiceme.api.discounts;

import com.invoiceme.application.discounts.list.ListDiscountCodesHandler;
import com.invoiceme.application.discounts.list.ListDiscountCodesHandler.DiscountCodeDto;
import com.invoiceme.application.discounts.validate.ValidateDiscountCodeHandler;
import com.invoiceme.application.discounts.validate.ValidateDiscountCodeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Discount Code operations.
 */
@RestController
@RequestMapping("/api/v1/discount-codes")
@Tag(name = "Discount Codes", description = "Discount code management API")
public class DiscountCodeController {
    
    private final ValidateDiscountCodeHandler validateDiscountCodeHandler;
    private final ListDiscountCodesHandler listDiscountCodesHandler;
    
    public DiscountCodeController(
            ValidateDiscountCodeHandler validateDiscountCodeHandler,
            ListDiscountCodesHandler listDiscountCodesHandler) {
        this.validateDiscountCodeHandler = validateDiscountCodeHandler;
        this.listDiscountCodesHandler = listDiscountCodesHandler;
    }
    
    @GetMapping("/validate/{code}")
    @Operation(summary = "Validate discount code", description = "Validates if a discount code exists and is active")
    @ApiResponse(responseCode = "200", description = "Validation result")
    public ResponseEntity<DiscountCodeValidationResponse> validateDiscountCode(
            @Parameter(description = "Discount code") @PathVariable String code) {
        ValidateDiscountCodeQuery query = new ValidateDiscountCodeQuery(code);
        var result = validateDiscountCodeHandler.handle(query);
        
        DiscountCodeValidationResponse response = new DiscountCodeValidationResponse(
            result.isValid(),
            result.message(),
            result.discountPercent() != null ? result.discountPercent() : null
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "List all discount codes", description = "Retrieves all discount codes")
    @ApiResponse(responseCode = "200", description = "Discount codes retrieved successfully")
    public ResponseEntity<List<DiscountCodeResponse>> listDiscountCodes() {
        var result = listDiscountCodesHandler.handle(new com.invoiceme.application.discounts.list.ListDiscountCodesQuery());
        
        List<DiscountCodeResponse> response = result.stream()
                .map(dto -> new DiscountCodeResponse(
                    dto.code(),
                    dto.discountPercent(),
                    dto.isActive(),
                    dto.createdAt(),
                    dto.updatedAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
    
    public record DiscountCodeValidationResponse(
        boolean isValid,
        String message,
        java.math.BigDecimal discountPercent
    ) {
    }
    
    public record DiscountCodeResponse(
        String code,
        java.math.BigDecimal discountPercent,
        boolean isActive,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt
    ) {
    }
}



