package com.stoikal.saktimart.productcategory.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CreateProductCategoryRequest(
        @NotBlank String name,
        String description,
        UUID idParent) {
}
