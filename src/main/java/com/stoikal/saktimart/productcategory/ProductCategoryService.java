package com.stoikal.saktimart.productcategory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.productcategory.dto.CreateProductCategoryRequest;
import com.stoikal.saktimart.productcategory.dto.ProductCategoryResponse;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    private ProductCategoryResponse toResponse(ProductCategoryEntity entity) {
        return new ProductCategoryResponse(
                entity.getIdProductCategory(),
                entity.getName(),
                entity.getDescription(),
                entity.getParent() != null ? entity.getParent().getIdProductCategory() : null);
    }

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public PaginatedResponse<ProductCategoryResponse> findAll(PageableRequest request) {
        Page<ProductCategoryEntity> page = productCategoryRepository.findAll(request.toPageable());

        List<ProductCategoryResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public ProductCategoryResponse findById(UUID id) {
        return productCategoryRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public ProductCategoryResponse create(CreateProductCategoryRequest request) {
        ProductCategoryEntity parent = null;

        if (request.idParent() != null) {
            parent = productCategoryRepository.findById(request.idParent())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        }

        ProductCategoryEntity newProductCategory = new ProductCategoryEntity(
                null,
                request.name(),
                request.description(),
                parent);

        return toResponse(productCategoryRepository.save(newProductCategory));
    }

    public void deleteById(UUID id) {
        if (!productCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product category not found");
        }

        if (productCategoryRepository.existsByParent_IdProductCategory(id)) {
            throw new IllegalStateException("Cannot delete category with children");
        }

        productCategoryRepository.deleteById(id);
    }
}
