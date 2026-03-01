package com.sakti.erp.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductScanRequest {
    private String code;
    private UUID priceTierId;
    private String priceTierName;
}
