package com.stoikal.saktimart.masterproductcategory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.masterproductcategory.dto.CreateMasterProductCategoryRequest;
import com.stoikal.saktimart.masterproductcategory.dto.MasterProductCategoryResponse;

@Service
public class MasterProductCategoryService {
    private final MasterProductCategoryRepository masterProductCategoryRepository;

    private MasterProductCategoryResponse toResponse(MasterProductCategoryEntity entity) {
        return new MasterProductCategoryResponse(
                entity.getIdProductCategory(),
                entity.getName(),
                entity.getDescription(),
                entity.getParent() != null ? entity.getParent().getIdProductCategory() : null);
    }

    public MasterProductCategoryService(MasterProductCategoryRepository masterProductCategoryRepository) {
        this.masterProductCategoryRepository = masterProductCategoryRepository;
    }

    public PaginatedResponse<MasterProductCategoryResponse> findAll(PageableRequest request) {
        Page<MasterProductCategoryEntity> page = masterProductCategoryRepository.findAll(request.toPageable());

        List<MasterProductCategoryResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public MasterProductCategoryResponse findById(UUID id) {
        return masterProductCategoryRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public MasterProductCategoryResponse create(CreateMasterProductCategoryRequest request) {
        MasterProductCategoryEntity parent = null;

        if (request.idParent() != null) {
            parent = masterProductCategoryRepository.findById(request.idParent())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        }

        MasterProductCategoryEntity newMasterProductCategory = new MasterProductCategoryEntity(
                null,
                request.name(),
                request.description(),
                parent);

        return toResponse(masterProductCategoryRepository.save(newMasterProductCategory));
    }

    public void deleteById(UUID id) {
        if (!masterProductCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product category not found");
        }

        if (masterProductCategoryRepository.existsByParent_IdProductCategory(id)) {
            throw new IllegalStateException("Cannot delete category with children");
        }

        masterProductCategoryRepository.deleteById(id);
    }
}
