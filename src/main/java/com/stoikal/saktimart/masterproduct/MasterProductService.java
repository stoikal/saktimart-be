package com.stoikal.saktimart.masterproduct;

import com.stoikal.saktimart.masterproductcategory.MasterProductCategoryEntity;
import com.stoikal.saktimart.masterproductcategory.MasterProductCategoryRepository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.masterproduct.dto.CreateMasterProductRequest;
import com.stoikal.saktimart.masterproduct.dto.MasterProductCategorySummary;
import com.stoikal.saktimart.masterproduct.dto.MasterProductResponse;

@Service
public class MasterProductService {
    private final MasterProductCategoryRepository masterProductCategoryRepository;
    private final MasterProductRepository masterProductRepository;

    private MasterProductCategorySummary toCategorySummary(MasterProductCategoryEntity category) {
        return new MasterProductCategorySummary(
                category.getIdProductCategory(),
                category.getName());
    }

    private MasterProductResponse toResponse(MasterProductEntity masterProduct) {
        return new MasterProductResponse(
                masterProduct.getIdProduct(),
                masterProduct.getSku(),
                masterProduct.getName(),
                masterProduct.getDescription(),
                masterProduct.getBarcode(),
                masterProduct.getCategories() != null
                        ? masterProduct.getCategories().stream().map(this::toCategorySummary).toList()
                        : null);
    }

    public MasterProductService(MasterProductRepository masterProductRepository, MasterProductCategoryRepository masterProductCategoryRepository) {
        this.masterProductRepository = masterProductRepository;
        this.masterProductCategoryRepository = masterProductCategoryRepository;
    }

    public PaginatedResponse<MasterProductResponse> findAll(PageableRequest request) {
        Page<MasterProductEntity> page = masterProductRepository.findAll(request.toPageable());

        List<MasterProductResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public MasterProductResponse findById(UUID id) {
        return masterProductRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public MasterProductResponse create(CreateMasterProductRequest request) {
        String normalizedSku = request.sku().trim().toUpperCase();

        // karena perlu cek keunikan sku. case-insensitive.
        if (masterProductRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new IllegalStateException("SKU already exists");
        }

        // karena perlu cek apakah product category yg diberikan ada
        if (request.categories() != null && !request.categories().isEmpty()) {
            for (UUID idProductCategory : request.categories()) {
                if (!masterProductCategoryRepository.existsById(idProductCategory)) {
                    throw new IllegalArgumentException("Category doesn't exist: " + idProductCategory);
                }
            }
        }

        MasterProductEntity newMasterProduct = new MasterProductEntity(
                null,
                normalizedSku,
                request.name(),
                request.description(),
                request.barcode());

        // untuk menghubungkan ke product category jika diberikan
        if (request.categories() != null && !request.categories().isEmpty()) {
            List<MasterProductCategoryEntity> categories = masterProductCategoryRepository.findAllById(request.categories());
            categories.forEach(newMasterProduct::addCategory);
        }

        return toResponse(masterProductRepository.save(newMasterProduct));
    }

    public void deleteById(UUID id) {
        if (!masterProductRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        masterProductRepository.deleteById(id);
    }
}
