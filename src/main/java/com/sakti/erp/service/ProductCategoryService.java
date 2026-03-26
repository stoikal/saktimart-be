package com.sakti.erp.service;

import com.sakti.erp.dto.ProductCategoryCreateRequest;
import com.sakti.erp.model.ProductCategory;
import com.sakti.erp.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository categoryRepository;

    public ProductCategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ProductCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<ProductCategory> findRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<ProductCategory> findSubCategories(UUID parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public List<ProductCategory> findAllDescendants(UUID categoryId) {
        return categoryRepository.findAllDescendants(categoryId);
    }

    @Transactional
    public ProductCategory createCategory(ProductCategoryCreateRequest request) {
        ProductCategory category = new ProductCategory();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            ProductCategory parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }
}
