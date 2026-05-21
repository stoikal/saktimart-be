package com.stoikal.saktimart.productcategory.dto;

import java.util.UUID;

public record ProductCategoryResponse(
        UUID id,
        String name,
        String description) {
}