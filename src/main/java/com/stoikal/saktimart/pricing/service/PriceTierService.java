package com.stoikal.saktimart.pricing.service;

import com.stoikal.saktimart.common.dto.PageableRequest;
import com.stoikal.saktimart.common.dto.PaginatedResponse;
import com.stoikal.saktimart.pricing.dto.CreatePriceTierRequest;
import com.stoikal.saktimart.pricing.dto.PriceTierResponse;
import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import com.stoikal.saktimart.pricing.repository.PriceTierRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceTierService {
    private final PriceTierRepository priceTierRepository;

    private PriceTierResponse toResponse(PriceTierEntity entity) {
        return new PriceTierResponse(
                entity.getIdPriceTier(),
                entity.getName(),
                entity.getDescription(),
                entity.getIsEnabled(),
                entity.getIsDeleted()
        );
    }

    public PriceTierService(PriceTierRepository priceTierRepository) {
        this.priceTierRepository = priceTierRepository;
    }

    public PaginatedResponse<PriceTierResponse> findAll(PageableRequest request) {
        Page<PriceTierEntity> page = priceTierRepository.findAll(request.toPageable());

        List<PriceTierResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                request.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PriceTierResponse create(CreatePriceTierRequest request) {
        PriceTierEntity entity = new PriceTierEntity(
                null,
                request.name(),
                request.description(),
                true,
                false
        );

        return toResponse(priceTierRepository.save(entity));
    }
}
