package com.stoikal.saktimart.pricing.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePriceTierRequest(
        @NotBlank String name,
        String description) {
}
