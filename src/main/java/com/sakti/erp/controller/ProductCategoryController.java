package com.sakti.erp.controller;

import com.sakti.erp.dto.ProductCategoryCreateRequest;
import com.sakti.erp.model.ProductCategory;
import com.sakti.erp.service.ProductCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/product-categories")
public class ProductCategoryController {
    private final ProductCategoryService categoryService;

    public ProductCategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<ProductCategory> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/roots")
    public List<ProductCategory> getRootCategories() {
        return categoryService.findRootCategories();
    }

    @GetMapping("/{parentId}/children")
    public List<ProductCategory> getSubCategories(@PathVariable UUID parentId) {
        return categoryService.findSubCategories(parentId);
    }

    @GetMapping("/{categoryId}/descendants")
    public List<ProductCategory> getAllDescendants(@PathVariable UUID categoryId) {
        return categoryService.findAllDescendants(categoryId);
    }

    @PostMapping
    public ResponseEntity<ProductCategory> createCategory(@Valid @RequestBody ProductCategoryCreateRequest request) {
        ProductCategory created = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
