package com.invoiceme.infrastructure.persistence.items;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for Item entity.
 */
@Repository
interface ItemJpaRepository extends JpaRepository<ItemEntity, UUID> {
    
    Page<ItemEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    @Query("SELECT COUNT(i) FROM ItemEntity i WHERE i.userId = :userId")
    long countByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(i) > 0 FROM ItemEntity i WHERE i.id = :itemId AND i.userId = :userId")
    boolean existsByIdAndUserId(@Param("itemId") UUID itemId, @Param("userId") UUID userId);
}









