package com.stoikal.saktimart.pricing.entity;

import com.stoikal.saktimart.common.entity.BaseEntity;
import com.stoikal.saktimart.product.entity.ProductEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(schema = "pricing", name = "product_price")
public class ProductPriceEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID idProductPrice;

    @ManyToOne
    @JoinColumn(name = "id_price_tier")
    private PriceTierEntity priceTier;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private ProductEntity product;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    protected ProductPriceEntity() {
    }

    public ProductPriceEntity(
            UUID idProductPrice,
            PriceTierEntity priceTier,
            ProductEntity product,
            LocalDateTime validFrom,
            LocalDateTime validTo
    ) {
        this.idProductPrice = idProductPrice;
        this.priceTier = priceTier;
        this.product = product;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public UUID getIdProductPrice() {
        return idProductPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public PriceTierEntity getPriceTier() {
        return priceTier;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
}
