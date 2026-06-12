package com.stoikal.saktimart.pricing.entity;

import com.stoikal.saktimart.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "pricing", name = "price_tier")
public class PriceTierEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID idPriceTier;

    private String name;

    private String description;

    private boolean isEnabled;

    private LocalDateTime deletedAt;

    protected PriceTierEntity() {
    }

    public PriceTierEntity(
            UUID idPriceTier,
            String name,
            String description,
            boolean isEnabled,
            LocalDateTime deletedAt
    ) {
        this.idPriceTier = idPriceTier;
        this.name = name;
        this.description = description;
        this.isEnabled = isEnabled;
        this.deletedAt = deletedAt;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
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

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
