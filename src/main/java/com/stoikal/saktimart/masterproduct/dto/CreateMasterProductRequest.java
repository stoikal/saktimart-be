package com.stoikal.saktimart.masterproduct.dto;

import java.util.List;
import java.util.UUID;

public record CreateMasterProductRequest(
        String sku,
        String name,
        String description,
        String barcode,
        List<UUID> categories) {
}
