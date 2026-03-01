package com.sakti.erp.controller;

import com.sakti.erp.dto.ProductCreateRequest;
import com.sakti.erp.dto.ProductScanRequest;
import com.sakti.erp.dto.ProductScanResponse;
import com.sakti.erp.model.Product;
import com.sakti.erp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAllProducts();
    }

    @PostMapping("/scan")
    public ResponseEntity<ProductScanResponse> scanProduct(@RequestBody ProductScanRequest request) {
        return productService.scanProduct(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductCreateRequest request) {
        productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
