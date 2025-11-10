package com.invoiceme.infrastructure.persistence.items;

import com.invoiceme.domain.items.Item;
import com.invoiceme.domain.items.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ItemRepository using Spring Data JPA.
 * Bridges domain repository interface with JPA implementation.
 */
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    
    private final ItemJpaRepository jpaRepository;
    
    public ItemRepositoryImpl(ItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Item save(Item item) {
        ItemEntity entity = ItemEntity.fromDomain(item);
        ItemEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Item> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(ItemEntity::toDomain);
    }
    
    @Override
    public List<Item> findByUserId(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemEntity> itemPage = jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return itemPage.getContent().stream()
                .map(ItemEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByUserId(UUID userId) {
        return jpaRepository.countByUserId(userId);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean belongsToUser(UUID itemId, UUID userId) {
        return jpaRepository.existsByIdAndUserId(itemId, userId);
    }
}



