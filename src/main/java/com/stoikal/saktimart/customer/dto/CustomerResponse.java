package com.stoikal.saktimart.customer.dto;

import java.util.UUID;

import jakarta.annotation.Nullable;

public record CustomerResponse(
        UUID idCustomer,
        String name,
        @Nullable UUID idPriceTier,
        @Nullable String priceTierName) {
}
