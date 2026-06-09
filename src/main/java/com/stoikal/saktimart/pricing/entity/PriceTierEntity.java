package com.stoikal.saktimart.pricing.entity;

import com.stoikal.saktimart.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(schema = "pricing", name = "priceTier")
public class PriceTierEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID idPriceTier;

    private String name;

    private String description;

    private boolean isEnabled;

    private boolean isDeleted;

    protected PriceTierEntity() {
    }

    public PriceTierEntity(
            UUID idPriceTier,
            String name,
            String description,
            boolean isEnabled,
            boolean isDeleted
    ) {
        this.idPriceTier = idPriceTier;
        this.name = name;
        this.description = description;
        this.isEnabled = isEnabled;
        this.isDeleted = isDeleted;
    }

    public UUID getIdPriceTier() {
        return idPriceTier;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsEnabled () {
        return isEnabled;
    }

    public boolean getIsDeleted () {
        return isDeleted;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public void setIsEnabled (boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setIsDeleted (boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
