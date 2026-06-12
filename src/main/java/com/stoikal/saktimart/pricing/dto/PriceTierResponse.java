package com.stoikal.saktimart.pricing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PriceTierResponse(
        UUID idPriceTier,
        String name,
        String description,
        boolean isEnabled,
        LocalDateTime deletedAt
) {
}
