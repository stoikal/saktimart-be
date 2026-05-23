package com.stoikal.saktimart.masterproduct.dto;

import java.util.List;
import java.util.UUID;

public record MasterProductResponse(
        UUID idProduct,
        String sku,
        String name,
        String description,
        String barcode,
        List<MasterProductCategorySummary> categories) {
}
