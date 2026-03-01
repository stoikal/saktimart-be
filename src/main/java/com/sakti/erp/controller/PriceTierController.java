package com.sakti.erp.controller;

import com.sakti.erp.model.PriceTier;
import com.sakti.erp.service.PriceTierService;
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
}
