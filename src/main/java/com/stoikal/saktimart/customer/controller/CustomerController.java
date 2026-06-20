package com.stoikal.saktimart.customer.controller;

import com.stoikal.saktimart.common.dto.ApiEnvelope;
import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.customer.dto.CreateCustomerRequest;
import com.stoikal.saktimart.customer.dto.CustomerResponse;
import com.stoikal.saktimart.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@Tag(name = "Customers", description = "CRUD for customers")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("")
    public ApiEnvelope<PaginatedResponse<CustomerResponse>> listCustomers(
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
                Set.of("createdAt", "updatedAt", "name"));

        return ApiEnvelope.success(service.findAll(request));
    }

    @GetMapping("/{id}")
    public ApiEnvelope<CustomerResponse> find(@PathVariable UUID id) {
        return ApiEnvelope.success(service.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<ApiEnvelope<CustomerResponse>> create(
            @Valid @RequestBody CreateCustomerRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiEnvelope.success(service.create(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiEnvelope<Void>> delete(@PathVariable UUID id) {
        service.softDeleteById(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiEnvelope.success(null));
    }
}
