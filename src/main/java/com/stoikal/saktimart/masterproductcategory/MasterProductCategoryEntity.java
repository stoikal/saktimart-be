package com.stoikal.saktimart.masterproductcategory;

import java.util.UUID;

import com.stoikal.saktimart.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "master", name = "product_category")
public class MasterProductCategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID idProductCategory;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "id_parent", foreignKey = @ForeignKey(name = "fk_product_category_parent"))
    private MasterProductCategoryEntity parent;

    protected MasterProductCategoryEntity() {
    }

    public MasterProductCategoryEntity(UUID idProductCategory, String name, String description,
            MasterProductCategoryEntity parent) {
        this.idProductCategory = idProductCategory;
        this.name = name;
        this.description = description;
        this.parent = parent;
    }

    public UUID getIdProductCategory() {
        return idProductCategory;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MasterProductCategoryEntity getParent() {
        return parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParent(MasterProductCategoryEntity parent) {
        this.parent = parent;
    }
}
