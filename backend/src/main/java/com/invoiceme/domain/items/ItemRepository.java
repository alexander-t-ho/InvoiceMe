package com.invoiceme.domain.items;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Item aggregate.
 * Defined in domain layer to maintain dependency inversion.
 */
public interface ItemRepository {
    
    /**
     * Saves an item.
     * @param item The item to save
     * @return The saved item
     */
    Item save(Item item);
    
    /**
     * Finds an item by ID.
     * @param id The item ID
     * @return Optional containing the item if found
     */
    Optional<Item> findById(UUID id);
    
    /**
     * Finds all items for a specific user with pagination.
     * @param userId The user ID
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of items
     */
    List<Item> findByUserId(UUID userId, int page, int size);
    
    /**
     * Counts total number of items for a user.
     * @param userId The user ID
     * @return Total count
     */
    long countByUserId(UUID userId);
    
    /**
     * Checks if an item exists by ID.
     * @param id The item ID
     * @return true if item exists
     */
    boolean existsById(UUID id);
    
    /**
     * Deletes an item by ID.
     * @param id The item ID
     */
    void deleteById(UUID id);
    
    /**
     * Checks if an item belongs to a user.
     * @param itemId The item ID
     * @param userId The user ID
     * @return true if item belongs to user
     */
    boolean belongsToUser(UUID itemId, UUID userId);
}









