package com.stoikal.saktimart.pricing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PriceTierResponse(
        UUID idPriceTier,
        String name,
        String description,
        Boolean isEnabled,
        Boolean isDefault,
        LocalDateTime deletedAt,
        Short sortOrder
) {
}
