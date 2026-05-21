package com.stoikal.saktimart.productcategory;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.productcategory.dto.ProductCategoryResponse;

@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {
    @GetMapping("")
    public List<ProductCategoryResponse> sayHello() {
        return List.of(
                new ProductCategoryResponse(UUID.randomUUID(), "Electronics", "Devices and gadgets"),
                new ProductCategoryResponse(UUID.randomUUID(), "Clothing", "Apparel and accessories"));
    }
}
