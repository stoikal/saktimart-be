package com.stoikal.saktimart.masterproduct;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterProductRepository extends JpaRepository<MasterProductEntity, UUID> {
    boolean existsBySkuIgnoreCase(String sku);
}
