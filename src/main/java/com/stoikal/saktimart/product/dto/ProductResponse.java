package com.stoikal.saktimart.product.dto;

import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID idProduct,
        String sku,
        String name,
        String description,
        String barcode,
        List<ProductCategorySummary> categories) {
}
