package com.stoikal.saktimart.customer.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerRequest(
        @NotBlank String name,
        @NotNull UUID idPriceTier) {
}
