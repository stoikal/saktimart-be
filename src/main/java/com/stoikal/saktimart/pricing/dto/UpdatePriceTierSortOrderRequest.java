package com.stoikal.saktimart.pricing.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record UpdatePriceTierSortOrderRequest(
        @NotEmpty List<UUID> ids
) {
}
