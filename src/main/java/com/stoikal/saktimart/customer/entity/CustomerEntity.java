package com.stoikal.saktimart.customer.entity;

import com.stoikal.saktimart.common.entity.BaseEntity;
import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "master", name = "customer")
public class CustomerEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID idCustomer;

    private String name;

    @ManyToOne
    @JoinColumn(name = "id_price_tier", foreignKey = @ForeignKey(name = "fk_customer_price_tier"))
    private PriceTierEntity priceTier;

    private LocalDateTime deletedAt;

    protected CustomerEntity() {
    }

    public CustomerEntity(UUID idCustomer, String name, PriceTierEntity priceTier) {
        this.idCustomer = idCustomer;
        this.name = name;
        this.priceTier = priceTier;
    }

    public UUID getIdCustomer() {
        return idCustomer;
    }

    public String getName() {
        return name;
    }

    public PriceTierEntity getPriceTier() {
        return priceTier;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriceTier(PriceTierEntity priceTier) {
        this.priceTier = priceTier;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
