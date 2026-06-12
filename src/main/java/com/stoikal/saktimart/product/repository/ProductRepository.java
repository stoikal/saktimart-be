package com.stoikal.saktimart.product.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stoikal.saktimart.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    @Query(value = "SELECT COUNT(*) > 0 FROM master.product WHERE UPPER(sku) = UPPER(:sku)", nativeQuery = true)
    boolean existsBySkuIgnoreCase(String sku);
}
