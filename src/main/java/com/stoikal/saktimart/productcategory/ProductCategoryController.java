package com.stoikal.saktimart.productcategory;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("")
    public ApiResponse<List<ProductCategoryResponse>> listProductCategories() {
        return ApiResponse.success(List.of(
                new ProductCategoryResponse(UUID.randomUUID(), "Electronics", "Devices and gadgets", null),
                new ProductCategoryResponse(UUID.randomUUID(), "Clothing", "Apparel and accessories", null)));
    }

    @PostMapping("")
    public ApiResponse<CreateProductCategoryRequest> create(@RequestBody CreateProductCategoryRequest entity) {

        return ApiResponse.success(entity);
    }

}
