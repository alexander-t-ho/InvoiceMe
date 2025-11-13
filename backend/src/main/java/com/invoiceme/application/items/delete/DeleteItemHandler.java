package com.invoiceme.application.items.delete;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.items.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for DeleteItemCommand.
 * Deletes an item from the item library.
 */
@Service
public class DeleteItemHandler {
    
    private final ItemRepository itemRepository;
    
    public DeleteItemHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    @Transactional
    public void handle(DeleteItemCommand command) {
        // Verify item exists and belongs to user
        if (!itemRepository.existsById(command.itemId())) {
            throw new DomainValidationException(
                "Item with ID " + command.itemId() + " not found"
            );
        }
        
        if (!itemRepository.belongsToUser(command.itemId(), command.userId())) {
            throw new DomainValidationException(
                "Item with ID " + command.itemId() + " does not belong to user"
            );
        }
        
        // Delete item
        itemRepository.deleteById(command.itemId());
    }
}









