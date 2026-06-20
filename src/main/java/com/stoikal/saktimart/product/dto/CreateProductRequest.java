package com.stoikal.saktimart.product.dto;

import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
        String sku,
        String name,
        String description,
        String barcode,
        List<UUID> categories,
        List<CreateProductPriceRequest> prices) {
}
