package com.sakti.erp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductCategoryCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private UUID parentId;
}
