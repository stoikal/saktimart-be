package com.stoikal.saktimart.product.dto;

import java.util.UUID;

public record ProductCategorySummary(
        UUID idProductCategory,
        String name) {
}
