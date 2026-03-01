package com.sakti.erp.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductCreateRequest {
    private String name;
    private String barcode;
    private String sku;
    private List<String> quickCodes;
}
