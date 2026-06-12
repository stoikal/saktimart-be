package com.stoikal.saktimart.product.service;

import com.stoikal.saktimart.product.entity.CategoryEntity;
import com.stoikal.saktimart.product.repository.CategoryRepository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.product.dto.CreateProductRequest;
import com.stoikal.saktimart.product.dto.CategorySummary;
import com.stoikal.saktimart.product.dto.ProductResponse;
import com.stoikal.saktimart.product.entity.ProductEntity;
import com.stoikal.saktimart.product.repository.ProductRepository;

@Service
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private CategorySummary toCategorySummary(CategoryEntity category) {
        return new CategorySummary(
                category.getIdProductCategory(),
                category.getName());
    }

    private ProductResponse toResponse(ProductEntity product) {
        return new ProductResponse(
                product.getIdProduct(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getBarcode(),
                product.getCategories() != null
                        ? product.getCategories().stream().map(this::toCategorySummary).toList()
                        : null);
    }

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public PaginatedResponse<ProductResponse> findAll(PageableRequest request) {
        Page<ProductEntity> page = productRepository.findAll(request.toPageable());

        List<ProductResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public ProductResponse findById(UUID id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public ProductResponse create(CreateProductRequest request) {
        String normalizedSku = request.sku().trim().toUpperCase();

        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new IllegalStateException("SKU already exists");
        }

        if (request.categories() != null && !request.categories().isEmpty()) {
            for (UUID idProductCategory : request.categories()) {
                if (!categoryRepository.existsById(idProductCategory)) {
                    throw new IllegalArgumentException("Category doesn't exist: " + idProductCategory);
                }
            }
        }

        ProductEntity newProduct = new ProductEntity(
                null,
                normalizedSku,
                request.name(),
                request.description(),
                request.barcode());

        if (request.categories() != null && !request.categories().isEmpty()) {
            List<CategoryEntity> categories = categoryRepository.findAllById(request.categories());
            categories.forEach(newProduct::addCategory);
        }

        return toResponse(productRepository.save(newProduct));
    }

    public void softDeleteById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepository.deleteById(id);
    }
}
