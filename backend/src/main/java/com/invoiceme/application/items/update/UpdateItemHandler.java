package com.invoiceme.application.items.update;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.items.Item;
import com.invoiceme.domain.items.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for UpdateItemCommand.
 * Updates an existing item in the item library.
 */
@Service
public class UpdateItemHandler {
    
    private final ItemRepository itemRepository;
    
    public UpdateItemHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    @Transactional
    public void handle(UpdateItemCommand command) {
        // Load item
        Item item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new DomainValidationException(
                    "Item with ID " + command.itemId() + " not found"
                ));
        
        // Verify ownership
        if (!item.getUserId().equals(command.userId())) {
            throw new DomainValidationException(
                "Item with ID " + command.itemId() + " does not belong to user"
            );
        }
        
        // Update item details
        item.updateDetails(command.description(), command.unitPrice());
        
        // Save item
        itemRepository.save(item);
    }
}



