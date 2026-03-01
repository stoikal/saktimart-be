package com.sakti.erp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "product_quick_codes", schema = "master")
@Data
public class ProductQuickCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    @JsonBackReference
    private Product product;

    // 1. Mandatory No-Args Constructor for JPA
    public ProductQuickCode() {}

    // 2. Custom Constructor for your Service logic
    public ProductQuickCode(Product product, String code) {
        this.product = product;
        this.code = code;
    }
}

