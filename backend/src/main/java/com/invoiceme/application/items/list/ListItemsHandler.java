package com.invoiceme.application.items.list;

import com.invoiceme.application.items.getById.GetItemByIdHandler.ItemDto;
import com.invoiceme.domain.items.Item;
import com.invoiceme.domain.items.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for ListItemsQuery.
 * Lists items for a user with pagination.
 */
@Service
public class ListItemsHandler {
    
    private final ItemRepository itemRepository;
    
    public ListItemsHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    @Transactional(readOnly = true)
    public PagedResult<ItemDto> handle(ListItemsQuery query) {
        List<Item> items = itemRepository.findByUserId(query.userId(), query.page(), query.size());
        long total = itemRepository.countByUserId(query.userId());
        
        List<ItemDto> content = items.stream()
                .map(item -> new ItemDto(
                    item.getId(),
                    item.getUserId(),
                    item.getDescription(),
                    item.getUnitPrice(),
                    item.getCreatedAt(),
                    item.getUpdatedAt()
                ))
                .toList();
        
        return new PagedResult<>(
            content,
            query.page(),
            query.size(),
            total
        );
    }
    
    public record PagedResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements
    ) {
    }
}

