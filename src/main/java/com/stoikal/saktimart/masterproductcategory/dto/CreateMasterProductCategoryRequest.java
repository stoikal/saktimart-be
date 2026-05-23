package com.stoikal.saktimart.masterproductcategory.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CreateMasterProductCategoryRequest(
        @NotBlank String name,
        String description,
        UUID idParent) {
}
