package com.stoikal.saktimart.product.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.product.dto.CreateCategoryRequest;
import com.stoikal.saktimart.product.dto.CategoryResponse;
import com.stoikal.saktimart.product.entity.CategoryEntity;
import com.stoikal.saktimart.product.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private CategoryResponse toResponse(CategoryEntity entity) {
        return new CategoryResponse(
                entity.getIdProductCategory(),
                entity.getName(),
                entity.getDescription(),
                entity.getParent() != null ? entity.getParent().getIdProductCategory() : null);
    }

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public PaginatedResponse<CategoryResponse> findAll(PageableRequest request) {
        Page<CategoryEntity> page = categoryRepository.findAll(request.toPageable());

        List<CategoryResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public CategoryResponse findById(UUID id) {
        return categoryRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public CategoryResponse create(CreateCategoryRequest request) {
        CategoryEntity parent = null;

        if (request.idParent() != null) {
            parent = categoryRepository.findById(request.idParent())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        }

        CategoryEntity newCategory = new CategoryEntity(
                null,
                request.name(),
                request.description(),
                true,
                parent);

        return toResponse(categoryRepository.save(newCategory));
    }

    public void deleteById(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product category not found");
        }

        if (categoryRepository.existsByParent_IdProductCategory(id)) {
            throw new IllegalStateException("Cannot delete category with children");
        }

        categoryRepository.deleteById(id);
    }
}
