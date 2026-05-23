package com.stoikal.saktimart.masterproduct;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.common.dto.ApiEnvelope;
import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.masterproduct.dto.CreateMasterProductRequest;
import com.stoikal.saktimart.masterproduct.dto.MasterProductResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Master Products", description = "CRUD for master products")
@RestController
@RequestMapping("/api/master/products")
public class MasterProductController {
    private final MasterProductService service;

    public MasterProductController(MasterProductService service) {
        this.service = service;
    }

    @GetMapping("")
    public ApiEnvelope<PaginatedResponse<MasterProductResponse>> listProducts(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "direction", required = false) String direction) {

        PageableRequest request = PageableRequest.of(
                page,
                size,
                sort,
                direction,
                "createdAt",
                Set.of("createdAt", "updatedAt", "name", "sku"));

        return ApiEnvelope.success(service.findAll(request));
    }

    @GetMapping("/{id}")
    public ApiEnvelope<MasterProductResponse> find(@PathVariable UUID id) {
        return ApiEnvelope.success(service.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<ApiEnvelope<MasterProductResponse>> create(@RequestBody CreateMasterProductRequest entity) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiEnvelope.success(service.create(entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiEnvelope<Void>> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiEnvelope.success(null));
    }

}
