package com.stoikal.saktimart.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.common.dto.ApiResponse;
import com.stoikal.saktimart.product.dto.ProductResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Products", description = "CRUD for products")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("")
    public ApiResponse<List<ProductResponse>> listProducts() {
        return ApiResponse.success(service.findAll());
    }
}
