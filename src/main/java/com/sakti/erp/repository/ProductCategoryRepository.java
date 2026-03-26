package com.sakti.erp.repository;

import com.sakti.erp.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    List<ProductCategory> findByParentIsNull();
    List<ProductCategory> findByParentId(UUID parentId);

    @Query(value = """
        WITH RECURSIVE category_tree AS (
            SELECT * FROM master.product_categories WHERE id = :categoryId
            UNION ALL
            SELECT c.* FROM master.product_categories c
            INNER JOIN category_tree ct ON c.parent_id = ct.id
        )
        SELECT * FROM category_tree
        """, nativeQuery = true)
    List<ProductCategory> findAllDescendants(@Param("categoryId") UUID categoryId);
}
