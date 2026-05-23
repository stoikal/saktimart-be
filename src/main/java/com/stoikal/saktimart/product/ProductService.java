package com.stoikal.saktimart.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stoikal.saktimart.product.dto.ProductResponse;

@Service
public class ProductService {
    private final ProductRepository repository;

    private ProductResponse toResponse(ProductEntity entity) {
        return new ProductResponse(
                entity.getIdProduct(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getBarcode(),
                entity.getCategories() != null ? entity.getCategories().stream().map(c -> c.getIdProductCategory()).toList() : null);
    }

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
}
