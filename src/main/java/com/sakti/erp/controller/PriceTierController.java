package com.sakti.erp.controller;

import com.sakti.erp.model.PriceTier;
import com.sakti.erp.service.PriceTierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/price-tiers")
public class PriceTierController {
    private final PriceTierService priceTierService;

    public PriceTierController(PriceTierService priceTierService) {
        this.priceTierService = priceTierService;
    }

    @GetMapping
    public List<PriceTier> getAllPriceTiers() {
        return priceTierService.getAllPriceTiers();
    }

    @PostMapping
    public ResponseEntity<PriceTier> createPriceTier(@RequestBody PriceTier priceTier) {
        return ResponseEntity.ok(priceTierService.createPriceTier(priceTier));
    }
}
