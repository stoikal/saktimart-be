package com.stoikal.saktimart.productcategory.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProductCategoryRequest(
        @NotBlank String name,
        String description,
        String idParent) {
}
