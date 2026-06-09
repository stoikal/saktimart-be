package com.stoikal.saktimart.product.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stoikal.saktimart.product.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByParent_IdProductCategory(UUID id);
}
