package com.stoikal.saktimart.productcategory;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, UUID> {
    boolean existsByParent_IdProductCategory(UUID id);
}
