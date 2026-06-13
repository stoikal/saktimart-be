package com.stoikal.saktimart.pricing.repository;

import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceTierRepository extends JpaRepository<PriceTierEntity, UUID> {
    @Query("SELECT MAX(p.sortOrder) FROM PriceTierEntity p")
    Optional<Short> findMaxSortOrder();

    List<PriceTierEntity> findByIsEnabledTrueAndDeletedAtIsNullOrderBySortOrderAsc();
}
