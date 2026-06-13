package com.stoikal.saktimart.pricing.service;

import com.stoikal.saktimart.pricing.dto.CreatePriceTierRequest;
import com.stoikal.saktimart.pricing.dto.PriceTierResponse;
import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import com.stoikal.saktimart.pricing.repository.PriceTierRepository;
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
                entity.getDeletedAt(),
                entity.getSortOrder()
        );
    }

    public PriceTierService(PriceTierRepository priceTierRepository) {
        this.priceTierRepository = priceTierRepository;
    }

    public List<PriceTierResponse> findAll() {
        return priceTierRepository.findByIsEnabledTrueAndDeletedAtIsNullOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PriceTierResponse create(CreatePriceTierRequest request) {
        Short sortOrder = (short) (priceTierRepository.findMaxSortOrder().orElse((short) 0) + 1);

        PriceTierEntity entity = new PriceTierEntity(
                null,
                request.name(),
                request.description(),
                true,
                null,
                sortOrder
        );

        return toResponse(priceTierRepository.save(entity));
    }
}
