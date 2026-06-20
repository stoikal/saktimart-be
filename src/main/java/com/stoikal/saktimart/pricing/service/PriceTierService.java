package com.stoikal.saktimart.pricing.service;

import com.stoikal.saktimart.pricing.dto.CreatePriceTierRequest;
import com.stoikal.saktimart.pricing.dto.PriceTierFilterRequest;
import com.stoikal.saktimart.pricing.dto.PriceTierResponse;
import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import com.stoikal.saktimart.pricing.repository.PriceTierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PriceTierService {
    private final PriceTierRepository priceTierRepository;

    private PriceTierResponse toResponse(PriceTierEntity entity) {
        return new PriceTierResponse(
                entity.getIdPriceTier(),
                entity.getName(),
                entity.getDescription(),
                entity.getIsEnabled(),
                entity.getIsDefault(),
                entity.getDeletedAt(),
                entity.getSortOrder()
        );
    }

    public PriceTierService(PriceTierRepository priceTierRepository) {
        this.priceTierRepository = priceTierRepository;
    }

    public List<PriceTierResponse> findAll(PriceTierFilterRequest filter) {
        return priceTierRepository.findAllFiltered(filter.isEnabled())
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

    @Transactional
    public void updateSortOrder(List<UUID> ids) {
        Map<UUID, PriceTierEntity> map = priceTierRepository.findAllById(ids)
                .stream()
                .filter(e -> e.getDeletedAt() == null)
                .collect(Collectors.toMap(PriceTierEntity::getIdPriceTier, e -> e));

        for (int i = 0; i < ids.size(); i++) {
            PriceTierEntity entity = map.get(ids.get(i));
            if (entity != null) {
                entity.setSortOrder((short) (i + 1));
            }
        }
    }
}
