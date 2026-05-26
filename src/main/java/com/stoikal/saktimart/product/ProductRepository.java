package com.stoikal.saktimart.product;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsBySkuIgnoreCase(String sku);
}
