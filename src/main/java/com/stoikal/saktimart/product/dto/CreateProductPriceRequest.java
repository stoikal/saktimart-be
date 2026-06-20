package com.stoikal.saktimart.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductPriceRequest(
        UUID idPriceTier,
        BigDecimal price) {
}
