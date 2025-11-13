package com.invoiceme.application.items.create;

import com.invoiceme.domain.items.Item;
import com.invoiceme.domain.items.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for CreateItemCommand.
 * Creates a new item in the item library.
 */
@Service
public class CreateItemHandler {
    
    private final ItemRepository itemRepository;
    
    public CreateItemHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    @Transactional
    public UUID handle(CreateItemCommand command) {
        // Create item using domain factory method
        Item item = Item.create(
            command.userId(),
            command.description(),
            command.unitPrice()
        );
        
        // Save item
        Item savedItem = itemRepository.save(item);
        
        return savedItem.getId();
    }
}









