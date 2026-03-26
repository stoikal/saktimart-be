package com.sakti.erp.service;

import com.sakti.erp.dto.ProductCreateRequest;
import com.sakti.erp.dto.ProductScanRequest;
import com.sakti.erp.dto.ProductScanResponse;
import com.sakti.erp.model.Product;
import com.sakti.erp.model.ProductPrice;
import com.sakti.erp.model.ProductQuickCode;
import com.sakti.erp.repository.ProductQuickCodeRepository;
import com.sakti.erp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductQuickCodeRepository productQuickCodeRepository;

    public ProductService(ProductRepository productRepository, ProductQuickCodeRepository productQuickCodeRepository) {
        this.productRepository = productRepository;
        this.productQuickCodeRepository = productQuickCodeRepository;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    @Transactional(readOnly = true)
    public Optional<ProductScanResponse> scanProduct(ProductScanRequest request) {
        Optional<Product> productOpt = productRepository.findByBarcodeOrQuickCode(request.getCode());

        if (productOpt.isEmpty()) {
            return Optional.empty();
        }

        Product product = productOpt.get();
        ProductScanResponse response = new ProductScanResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setBarcode(product.getBarcode());

        // Find price for the requested tier
        if (product.getPrices() != null) {
            LocalDateTime now = LocalDateTime.now();

            // 1. Try to find the Requested Tier (e.g., "Wholesale")
            Optional<ProductPrice> priceOpt = findActivePrice(product, request.getTier(), now);

            // 2. FALLBACK: If not found, try to find the Default Tier
            if (priceOpt.isEmpty()) {
                priceOpt = findActiveDefaultPrice(product, now);
            }

            // 3. Set the response
            priceOpt.ifPresent(price -> {
                response.setPrice(price.getPrice());
                response.setPriceTierId(price.getPriceTier().getId());
                response.setPriceTierName(price.getPriceTier().getName());
            });
        }

        return Optional.of(response);
    }

    private Optional<ProductPrice> findActivePrice(Product product, String tierName, LocalDateTime now) {
        return product.getPrices().stream()
                .filter(p -> p.getPriceTier().getName().equalsIgnoreCase(tierName))
                .filter(p -> isWithinDateRange(p, now))
                .findFirst();
    }

    private Optional<ProductPrice> findActiveDefaultPrice(Product product, LocalDateTime now) {
        return product.getPrices().stream()
                .filter(p -> p.getPriceTier().getIsDefault()) // Using the is_default column we created
                .filter(p -> isWithinDateRange(p, now))
                .findFirst();
    }

    private boolean isWithinDateRange(ProductPrice p, LocalDateTime now) {
        return (p.getValidFrom() == null || !p.getValidFrom().isAfter(now)) &&
                (p.getValidTo() == null || !p.getValidTo().isBefore(now));
    }

    @Transactional
    public void createProduct(ProductCreateRequest req) {
        // 1. Save the Product Identity
        Product product = new Product();
        product.setName(req.getName());
        product.setBarcode(req.getBarcode());
        product.setSku(req.getSku());
        Product savedProduct = productRepository.save(product);

        // 2. Save the list of Quick Codes
        List<String> quickCodes = req.getQuickCodes();
        if (quickCodes!= null && !quickCodes.isEmpty()) {
            List<ProductQuickCode> codes = quickCodes.stream()
                    .filter(code -> !code.isBlank()) // Ensure no empty strings are saved
                    .map(code -> new ProductQuickCode(savedProduct, code))
                    .toList();
            productQuickCodeRepository.saveAll(codes);
        }
    }
}
