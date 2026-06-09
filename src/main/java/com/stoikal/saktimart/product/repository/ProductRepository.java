package com.stoikal.saktimart.product.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stoikal.saktimart.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsBySkuIgnoreCase(String sku);
}
