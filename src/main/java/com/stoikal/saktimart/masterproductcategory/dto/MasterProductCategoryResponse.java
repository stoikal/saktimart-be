package com.stoikal.saktimart.masterproductcategory.dto;

import java.util.UUID;

import jakarta.annotation.Nullable;

public record MasterProductCategoryResponse(
        UUID idProductCategory,
        String name,
        String description,
        @Nullable UUID idParent) {
}
