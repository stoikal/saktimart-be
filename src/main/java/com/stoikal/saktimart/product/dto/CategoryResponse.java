package com.stoikal.saktimart.product.dto;

import java.util.UUID;

import jakarta.annotation.Nullable;

public record CategoryResponse(
        UUID idProductCategory,
        String name,
        String description,
        @Nullable UUID idParent) {
}
