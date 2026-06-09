package com.stoikal.saktimart.pricing.repository;

import com.stoikal.saktimart.pricing.entity.PriceTierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PriceTierRepository extends JpaRepository<PriceTierEntity, UUID> {
}
