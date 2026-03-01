package com.sakti.erp.repository;

import com.sakti.erp.model.PriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PriceTierRepository extends JpaRepository<PriceTier, UUID> {
}
