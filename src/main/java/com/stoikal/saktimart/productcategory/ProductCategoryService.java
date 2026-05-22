package com.stoikal.saktimart.productcategory;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stoikal.saktimart.productcategory.dto.CreateProductCategoryRequest;
import com.stoikal.saktimart.productcategory.dto.ProductCategoryResponse;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository repository;

    private ProductCategoryResponse toResponse(ProductCategoryEntity entity) {
        return new ProductCategoryResponse(
                entity.getIdProductCategory(),
                entity.getName(),
                entity.getDescription(),
                entity.getParent() != null ? entity.getParent().getIdProductCategory() : null);
    }

    public ProductCategoryService(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    public List<ProductCategoryResponse> findAll() {
        return repository.findAll().stream()
                .map(entity -> new ProductCategoryResponse(
                        entity.getIdProductCategory(),
                        entity.getName(),
                        entity.getDescription(),
                        entity.getParent() != null ? entity.getParent().getIdProductCategory() : null))
                .toList();
    }

    public ProductCategoryResponse create(CreateProductCategoryRequest request) {
        ProductCategoryEntity parent = null;
        if (request.idParent() != null) {
            parent = repository.findById(request.idParent())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        }
        ProductCategoryEntity entity = new ProductCategoryEntity(
                null,
                request.name(),
                request.description(),
                parent);

        return toResponse(repository.save(entity));
    }

    public void delete(UUID id) {
        if (repository.existsByParent_IdProductCategory(id)) {
            throw new IllegalStateException("Cannot delete category with children");
        }

        repository.deleteById(id);
    }
}