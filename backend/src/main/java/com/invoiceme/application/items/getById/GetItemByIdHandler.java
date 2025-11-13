package com.invoiceme.application.items.getById;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.items.Item;
import com.invoiceme.domain.items.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for GetItemByIdQuery.
 * Retrieves an item by ID.
 */
@Service
public class GetItemByIdHandler {
    
    private final ItemRepository itemRepository;
    
    public GetItemByIdHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    @Transactional(readOnly = true)
    public ItemDto handle(GetItemByIdQuery query) {
        Item item = itemRepository.findById(query.itemId())
                .orElseThrow(() -> new DomainValidationException(
                    "Item with ID " + query.itemId() + " not found"
                ));
        
        // Verify ownership
        if (!item.getUserId().equals(query.userId())) {
            throw new DomainValidationException(
                "Item with ID " + query.itemId() + " does not belong to user"
            );
        }
        
        return new ItemDto(
            item.getId(),
            item.getUserId(),
            item.getDescription(),
            item.getUnitPrice(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
    
    public record ItemDto(
        UUID id,
        UUID userId,
        String description,
        java.math.BigDecimal unitPrice,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt
    ) {
    }
}









