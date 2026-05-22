package com.stoikal.saktimart.productcategory;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stoikal.saktimart.productcategory.dto.CreateProductCategoryRequest;
import com.stoikal.saktimart.productcategory.dto.ProductCategoryResponse;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository repository;

    private ProductCategoryResponse toResponse(ProductCategoryEntity entity) {
        return new ProductCategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                null);
    }

    public ProductCategoryService(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    public List<ProductCategoryResponse> findAll() {
        return repository.findAll().stream()
                .map(entity -> new ProductCategoryResponse(entity.getId(), entity.getName(), entity.getDescription(),
                        null))
                .toList();
    }

    public ProductCategoryResponse create(CreateProductCategoryRequest request) {
        ProductCategoryEntity entity = new ProductCategoryEntity(
                null,
                request.name(),
                request.description(),
                null);

        return toResponse(repository.save(entity));
    }
}