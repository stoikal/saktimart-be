package com.stoikal.saktimart.productcategory;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "master", name = "product_category")
public class ProductCategoryEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    protected ProductCategoryEntity() {
    }

    public ProductCategoryEntity(UUID id, String name, String description, ProductCategoryEntity parent) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
