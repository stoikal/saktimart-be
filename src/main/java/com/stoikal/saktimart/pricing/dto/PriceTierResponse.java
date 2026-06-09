package com.stoikal.saktimart.pricing.dto;

import java.util.UUID;

public record PriceTierResponse(
        UUID idPriceTier,
        String name,
        String description,
        boolean isEnabled,
        boolean isDeleted
) {
}
