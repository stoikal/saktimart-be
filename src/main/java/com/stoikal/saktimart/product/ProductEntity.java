package com.stoikal.saktimart.product;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.stoikal.saktimart.common.entity.BaseEntity;
import com.stoikal.saktimart.productcategory.ProductCategoryEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(schema = "master", name = "product")
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID idProduct;

    @Column(unique = true)
    private String sku;

    private String name;

    private String description;

    private String barcode;

    @ManyToMany
    @JoinTable(
        schema = "master",
        name = "product_category_mapping",
        joinColumns = @JoinColumn(name = "id_product"),
        inverseJoinColumns = @JoinColumn(name = "id_product_category"))
    private Set<ProductCategoryEntity> categories = new HashSet<>();

    protected ProductEntity() {
    }

    public ProductEntity(
            UUID idProduct,
            String sku,
            String name,
            String description,
            String barcode) {
        this.idProduct = idProduct;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
    }

    public UUID getIdProduct() {
        return idProduct;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBarcode() {
        return barcode;
    }

    public Set<ProductCategoryEntity> getCategories() {
        return categories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void addCategory(ProductCategoryEntity category) {
        this.categories.add(category);
    }

    public void removeCategory(ProductCategoryEntity category) {
        this.categories.remove(category);
    }
}
