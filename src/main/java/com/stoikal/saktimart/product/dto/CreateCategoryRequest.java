package com.stoikal.saktimart.product.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank String name,
        String description,
        UUID idParent) {
}
