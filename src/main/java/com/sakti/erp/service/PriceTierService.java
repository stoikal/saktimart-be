package com.sakti.erp.service;

import com.sakti.erp.model.PriceTier;
import com.sakti.erp.repository.PriceTierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceTierService {
    private final PriceTierRepository priceTierRepository;

    public PriceTierService(PriceTierRepository priceTierRepository) {
        this.priceTierRepository = priceTierRepository;
    }

    public List<PriceTier> getAllPriceTiers() {
        return priceTierRepository.findAll();
    }

    public PriceTier createPriceTier(PriceTier priceTier) {
        return priceTierRepository.save(priceTier);
    }
}
