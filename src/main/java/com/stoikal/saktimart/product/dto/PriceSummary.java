package com.stoikal.saktimart.product.dto;

import java.math.BigDecimal;

public record PriceSummary(
        String tierName,
        BigDecimal price
) {
}
