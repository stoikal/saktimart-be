package com.stoikal.saktimart.pricing.repository;

import com.stoikal.saktimart.pricing.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProductPriceRepository extends JpaRepository<ProductPriceEntity, UUID> {
    @Query("SELECT pp FROM ProductPriceEntity pp " +
            "JOIN FETCH pp.priceTier " +
            "WHERE pp.product.idProduct IN :productIds " +
            "AND pp.validFrom <= :now " +
            "AND (pp.validTo IS NULL OR pp.validTo > :now)")
    List<ProductPriceEntity> findActiveByProductIds(
            @Param("productIds") List<UUID> productIds,
            @Param("now") LocalDateTime now);
}
