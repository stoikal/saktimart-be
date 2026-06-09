package com.stoikal.saktimart.pricing.controller;

import com.stoikal.saktimart.common.dto.ApiEnvelope;
import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.pricing.dto.PriceTierResponse;
import com.stoikal.saktimart.pricing.service.PriceTierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "Price Tier", description = "Price Tier")
@RestController
@RequestMapping("/api/pricing/price-tier")
public class PriceTierController {
    private final PriceTierService priceTierService;

    public PriceTierController(PriceTierService priceTierService) {
        this.priceTierService = priceTierService;
    }

    @GetMapping("")
    public ApiEnvelope<PaginatedResponse<PriceTierResponse>> listPriceTiers(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "direction", required = false) String direction
    ) {

        PageableRequest request = PageableRequest.of(page, size, sort, direction, "createdAt", Set.of("name"));
        return ApiEnvelope.success(priceTierService.findAll(request));
    }
}
