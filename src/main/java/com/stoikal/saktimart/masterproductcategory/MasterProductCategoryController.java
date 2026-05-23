package com.stoikal.saktimart.masterproductcategory;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.common.dto.ApiEnvelope;
import com.stoikal.saktimart.masterproductcategory.dto.CreateMasterProductCategoryRequest;
import com.stoikal.saktimart.masterproductcategory.dto.MasterProductCategoryResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Master Product Categories", description = "CRUD for master product categories")
@RestController
@RequestMapping("/api/master/product-categories")
public class MasterProductCategoryController {
    private final MasterProductCategoryService service;

    public MasterProductCategoryController(MasterProductCategoryService service) {
        this.service = service;
    }

    @GetMapping("")
    public ApiEnvelope<List<MasterProductCategoryResponse>> listProductCategories() {
        return ApiEnvelope.success(service.findAll());
    }

    @GetMapping("/{id}")
    public ApiEnvelope<MasterProductCategoryResponse> find(@PathVariable UUID id) {
        return ApiEnvelope.success(service.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<ApiEnvelope<MasterProductCategoryResponse>> create(
            @RequestBody CreateMasterProductCategoryRequest entity) {

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
