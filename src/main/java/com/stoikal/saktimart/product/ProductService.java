package com.stoikal.saktimart.product;

import com.stoikal.saktimart.productcategory.ProductCategoryEntity;
import com.stoikal.saktimart.productcategory.ProductCategoryRepository;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stoikal.saktimart.product.dto.CreateProductRequest;
import com.stoikal.saktimart.product.dto.ProductResponse;

@Service
public class ProductService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    private ProductResponse toResponse(ProductEntity product) {
        return new ProductResponse(
                product.getIdProduct(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getBarcode(),
                product.getCategories() != null
                        ? product.getCategories().stream().map(c -> c.getIdProductCategory()).toList()
                        : null);
    }

    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse create(CreateProductRequest request) {
        String normalizedSku = request.sku().trim().toUpperCase();

        // karena perlu cek keunikan sku. case-insensitive.
        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new IllegalStateException("SKU already exists");
        }

        // karena perlu cek apakah product category yg diberikan ada
        if (request.categories() != null && !request.categories().isEmpty()) {
            for (UUID idProductCategory: request.categories()) {
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

        // untuk link ke product category jika diberikan
        if (request.categories() != null && !request.categories().isEmpty()) {
            List<ProductCategoryEntity> categories = productCategoryRepository.findAllById(request.categories());
            categories.forEach(newProduct::addCategory);
        }

        return toResponse(productRepository.save(newProduct));
    }
}
