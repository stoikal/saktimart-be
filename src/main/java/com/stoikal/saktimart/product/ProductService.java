package com.stoikal.saktimart.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stoikal.saktimart.product.dto.CreateProductRequest;
import com.stoikal.saktimart.product.dto.ProductResponse;

@Service
public class ProductService {
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

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse create(CreateProductRequest request) {
        String normalizedSku = request.sku().trim().toUpperCase();

        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new IllegalArgumentException("SKU already exists");
        }

        ProductEntity newProduct = new ProductEntity(
                null,
                normalizedSku,
                request.name(),
                request.description(),
                request.barcode());

        return toResponse(productRepository.save(newProduct));
    }
}
