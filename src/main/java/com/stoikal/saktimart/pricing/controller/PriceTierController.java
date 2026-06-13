package com.stoikal.saktimart.pricing.controller;

import com.stoikal.saktimart.common.dto.ApiEnvelope;
import com.stoikal.saktimart.pricing.dto.CreatePriceTierRequest;
import com.stoikal.saktimart.pricing.dto.PriceTierResponse;
import com.stoikal.saktimart.pricing.service.PriceTierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Price Tier", description = "Price Tier")
@RestController
@RequestMapping("/api/price-tiers")
public class PriceTierController {
    private final PriceTierService priceTierService;

    public PriceTierController(PriceTierService priceTierService) {
        this.priceTierService = priceTierService;
    }

    @GetMapping("")
    public ApiEnvelope<List<PriceTierResponse>> listPriceTiers() {
        return ApiEnvelope.success(priceTierService.findAll());
    }

    @PostMapping("")
    public ResponseEntity<ApiEnvelope<PriceTierResponse>> create(
            @Valid @RequestBody CreatePriceTierRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiEnvelope.success(priceTierService.create(request)));
    }
}
