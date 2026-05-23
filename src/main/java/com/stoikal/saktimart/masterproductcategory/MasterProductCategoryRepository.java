package com.stoikal.saktimart.masterproductcategory;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterProductCategoryRepository extends JpaRepository<MasterProductCategoryEntity, UUID> {
    boolean existsByParent_IdProductCategory(UUID id);
}
