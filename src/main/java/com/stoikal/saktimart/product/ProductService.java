package com.stoikal.saktimart.product;

import com.stoikal.saktimart.productcategory.ProductCategoryEntity;
import com.stoikal.saktimart.productcategory.ProductCategoryRepository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.product.dto.CreateProductRequest;
import com.stoikal.saktimart.product.dto.ProductCategorySummary;
import com.stoikal.saktimart.product.dto.ProductResponse;

@Service
public class ProductService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    private ProductCategorySummary toCategorySummary(ProductCategoryEntity category) {
        return new ProductCategorySummary(
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

    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
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

        // karena perlu cek keunikan sku. case-insensitive.
        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new IllegalStateException("SKU already exists");
        }

        // karena perlu cek apakah product category yg diberikan ada
        if (request.categories() != null && !request.categories().isEmpty()) {
            for (UUID idProductCategory : request.categories()) {
                if (!productCategoryRepository.existsById(idProductCategory)) {
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

        // untuk menghubungkan ke product category jika diberikan
        if (request.categories() != null && !request.categories().isEmpty()) {
            List<ProductCategoryEntity> categories = productCategoryRepository.findAllById(request.categories());
            categories.forEach(newProduct::addCategory);
        }

        return toResponse(productRepository.save(newProduct));
    }

    public void deleteById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepository.deleteById(id);
    }
}
