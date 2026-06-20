package com.stoikal.saktimart.product.service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import com.stoikal.saktimart.pricing.entity.ProductPriceEntity;
import com.stoikal.saktimart.pricing.repository.PriceTierRepository;
import com.stoikal.saktimart.pricing.repository.ProductPriceRepository;
import com.stoikal.saktimart.product.dto.CategorySummary;
import com.stoikal.saktimart.product.dto.CreateProductPriceRequest;
import com.stoikal.saktimart.product.dto.CreateProductRequest;
import com.stoikal.saktimart.product.dto.PriceSummary;
import com.stoikal.saktimart.product.dto.ProductResponse;
import com.stoikal.saktimart.product.entity.CategoryEntity;
import com.stoikal.saktimart.product.entity.ProductEntity;
import com.stoikal.saktimart.product.repository.CategoryRepository;
import com.stoikal.saktimart.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    private final ProductPriceRepository productPriceRepository;

    private final PriceTierRepository priceTierRepository;

    private CategorySummary toCategorySummary(CategoryEntity category) {
        return new CategorySummary(
                category.getIdProductCategory(),
                category.getName());
    }

    private PriceSummary toPriceSummary(ProductPriceEntity price) {
        return new PriceSummary(
                price.getPriceTier().getName(),
                price.getPrice());
    }

    private ProductResponse toResponse(ProductEntity product, List<PriceSummary> prices) {
        return new ProductResponse(
                product.getIdProduct(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getBarcode(),
                product.getCategories() != null
                        ? product.getCategories().stream().map(this::toCategorySummary).toList()
                        : null,
                prices
        );
    }

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductPriceRepository productPriceRepository, PriceTierRepository priceTierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productPriceRepository = productPriceRepository;
        this.priceTierRepository = priceTierRepository;
    }

    public PaginatedResponse<ProductResponse> findAll(PageableRequest request) {
        Page<ProductEntity> page = productRepository.findAll(request.toPageable());

        List<UUID> productIds = page.getContent().stream()
                .map(ProductEntity::getIdProduct)
                .toList();


        Map<UUID, List<PriceSummary>> pricesByProduct = productIds.isEmpty()
                ? Collections.emptyMap()
                : productPriceRepository.findActiveByProductIds(productIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.groupingBy(
                        pp -> pp.getProduct().getIdProduct(),
                        Collectors.mapping(this::toPriceSummary, Collectors.toList())));


        List<ProductResponse> content = page.getContent().stream()
                .map(p -> toResponse(p, pricesByProduct.getOrDefault(p.getIdProduct(), List.of())))
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public ProductResponse findById(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<PriceSummary> prices = productPriceRepository
                .findActiveByProductIds(List.of(id), LocalDateTime.now())
                .stream()
                .map(this::toPriceSummary)
                .toList();

        return toResponse(product, prices);

    }

    @Transactional
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

        // pengecekan apakah price tier id yang diterima ada di db
        if (request.prices() != null && !request.prices().isEmpty()) {
            for (CreateProductPriceRequest priceReq : request.prices()) {
                if (!priceTierRepository.existsById(priceReq.idPriceTier())) {
                    throw new IllegalArgumentException("Price tier doesn't exist: " + priceReq.idPriceTier());
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

        ProductEntity savedProduct = productRepository.save(newProduct);

        //
        List<PriceSummary> priceSummaries = new ArrayList<>();
        if (request.prices() != null && !request.prices().isEmpty()) {
            Map<UUID, PriceTierEntity> tierMap = priceTierRepository.findAllById(
                    request.prices().stream().map(CreateProductPriceRequest::idPriceTier).toList()
            ).stream().collect(Collectors.toMap(PriceTierEntity::getIdPriceTier, t -> t));

            LocalDateTime now = LocalDateTime.now();
            for (CreateProductPriceRequest priceReq : request.prices()) {
                PriceTierEntity tier = tierMap.get(priceReq.idPriceTier());
                ProductPriceEntity priceEntity = new ProductPriceEntity(
                        null,
                        tier,
                        savedProduct,
                        now,
                        null);
                priceEntity.setPrice(priceReq.price());
                productPriceRepository.save(priceEntity);
                priceSummaries.add(new PriceSummary(tier.getName(), priceReq.price()));
            }
        }

        return toResponse(savedProduct, priceSummaries);
    }

    public void softDeleteById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepository.deleteById(id);
    }
}
