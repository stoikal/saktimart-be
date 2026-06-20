package com.stoikal.saktimart.customer.service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.common.exception.ResourceNotFoundException;
import com.stoikal.saktimart.customer.dto.CreateCustomerRequest;
import com.stoikal.saktimart.customer.dto.CustomerResponse;
import com.stoikal.saktimart.customer.entity.CustomerEntity;
import com.stoikal.saktimart.customer.repository.CustomerRepository;
import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import com.stoikal.saktimart.pricing.repository.PriceTierRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PriceTierRepository priceTierRepository;

    private CustomerResponse toResponse(CustomerEntity entity) {
        return new CustomerResponse(
                entity.getIdCustomer(),
                entity.getName(),
                entity.getPriceTier() != null ? entity.getPriceTier().getIdPriceTier() : null,
                entity.getPriceTier() != null ? entity.getPriceTier().getName() : null);
    }

    public CustomerService(CustomerRepository customerRepository, PriceTierRepository priceTierRepository) {
        this.customerRepository = customerRepository;
        this.priceTierRepository = priceTierRepository;
    }

    public PaginatedResponse<CustomerResponse> findAll(PageableRequest request) {
        Page<CustomerEntity> page = customerRepository.findAllActive(request.toPageable());

        List<CustomerResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public CustomerResponse findById(UUID id) {
        return customerRepository.findActiveById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public CustomerResponse create(CreateCustomerRequest request) {
        PriceTierEntity priceTier = null;

        if (request.idPriceTier() != null) {
            priceTier = priceTierRepository.findById(request.idPriceTier())
                    .orElseThrow(() -> new IllegalArgumentException("Price tier not found"));
        }

        CustomerEntity entity = new CustomerEntity(
                null,
                request.name(),
                priceTier);

        return toResponse(customerRepository.save(entity));
    }

    @Transactional
    public void softDeleteById(UUID id) {
        CustomerEntity entity = customerRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        entity.setDeletedAt(LocalDateTime.now());
    }
}
