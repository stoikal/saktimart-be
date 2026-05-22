package com.stoikal.saktimart.productcategory;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.common.dto.ApiResponse;
import com.stoikal.saktimart.productcategory.dto.CreateProductCategoryRequest;
import com.stoikal.saktimart.productcategory.dto.ProductCategoryResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Product Categories", description = "CRUD for product categories")
@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {
    private final ProductCategoryService service;

    public ProductCategoryController(ProductCategoryService service) {
        this.service = service;
    }

    @GetMapping("")
    public ApiResponse<List<ProductCategoryResponse>> listProductCategories() {
        return ApiResponse.success(service.findAll());
    }

    @PostMapping("")
    public ApiResponse<ProductCategoryResponse> create(@RequestBody CreateProductCategoryRequest entity) {

        return ApiResponse.success(service.create(entity));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.success(null);
    }

}
