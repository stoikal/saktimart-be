package com.stoikal.saktimart.product.dto;

import java.util.UUID;

public record CategorySummary(
        UUID idProductCategory,
        String name) {
}
