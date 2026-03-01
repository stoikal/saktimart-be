package com.sakti.erp.service;

import com.sakti.erp.dto.ProductScanRequest;
import com.sakti.erp.dto.ProductScanResponse;
import com.sakti.erp.model.Product;
import com.sakti.erp.model.ProductPrice;
import com.sakti.erp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
            Optional<ProductPrice> priceOpt = product.getPrices().stream()
                    .filter(p -> {
                        if (request.getPriceTierId() != null) {
                            return p.getPriceTier().getId().equals(request.getPriceTierId());
                        } else if (request.getPriceTierName() != null) {
                            return p.getPriceTier().getName().equalsIgnoreCase(request.getPriceTierName());
                        }
                        return false;
                    })
                    .filter(p -> (p.getValidFrom() == null || !p.getValidFrom().isAfter(now)) &&
                                 (p.getValidTo() == null || !p.getValidTo().isBefore(now)))
                    .findFirst();

            if (priceOpt.isPresent()) {
                ProductPrice price = priceOpt.get();
                response.setPrice(price.getPrice());
                response.setPriceTierId(price.getPriceTier().getId());
                response.setPriceTierName(price.getPriceTier().getName());
            }
        }

        return Optional.of(response);
    }
}
