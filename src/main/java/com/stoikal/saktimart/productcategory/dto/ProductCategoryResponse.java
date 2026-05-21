package com.stoikal.saktimart.productcategory.dto;

import java.util.UUID;

import jakarta.annotation.Nullable;

public record ProductCategoryResponse(
        UUID id,
        String name,
        String description,
        @Nullable UUID idParent) {
}