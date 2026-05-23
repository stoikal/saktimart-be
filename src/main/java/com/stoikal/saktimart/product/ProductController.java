package com.stoikal.saktimart.product;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.common.dto.ApiEnvelope;
import com.stoikal.saktimart.product.dto.CreateProductRequest;
import com.stoikal.saktimart.product.dto.ProductResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Products", description = "CRUD for products")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("")
    public ApiEnvelope<List<ProductResponse>> listProducts() {
        return ApiEnvelope.success(service.findAll());
    }

    @GetMapping("/{id}")
    public ApiEnvelope<ProductResponse> find(@PathVariable UUID id) {
        return ApiEnvelope.success(service.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<ApiEnvelope<ProductResponse>> create(@RequestBody CreateProductRequest entity) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiEnvelope.success(service.create(entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiEnvelope<Void>> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiEnvelope.success(null));
    }

}
