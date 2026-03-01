package com.sakti.erp.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductScanResponse {
    private UUID id;
    private String sku;
    private String name;
    private String barcode;
    private Long price;
    private UUID priceTierId;
    private String priceTierName;
}
