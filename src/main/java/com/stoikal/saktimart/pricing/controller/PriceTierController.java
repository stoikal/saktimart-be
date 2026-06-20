package com.stoikal.saktimart.pricing.controller;

import com.stoikal.saktimart.common.dto.ApiEnvelope;
import com.stoikal.saktimart.pricing.dto.CreatePriceTierRequest;
import com.stoikal.saktimart.pricing.dto.PriceTierFilterRequest;
import com.stoikal.saktimart.pricing.dto.PriceTierResponse;
import com.stoikal.saktimart.pricing.dto.UpdatePriceTierSortOrderRequest;
import com.stoikal.saktimart.pricing.service.PriceTierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ApiEnvelope<List<PriceTierResponse>> listPriceTiers(
            @RequestParam(required = false) Boolean isEnabled
    ) {
        PriceTierFilterRequest filter = new PriceTierFilterRequest(isEnabled);
        return ApiEnvelope.success(priceTierService.findAll(filter));
    }

    @PostMapping("")
    public ResponseEntity<ApiEnvelope<PriceTierResponse>> create(
            @Valid @RequestBody CreatePriceTierRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiEnvelope.success(priceTierService.create(request)));
    }

    @PutMapping("/sort-order")
    public ResponseEntity<ApiEnvelope<Void>> updateSortOrder(
            @Valid @RequestBody UpdatePriceTierSortOrderRequest request) {
        priceTierService.updateSortOrder(request.ids());
        return ResponseEntity.ok(ApiEnvelope.success(null));
    }
}
