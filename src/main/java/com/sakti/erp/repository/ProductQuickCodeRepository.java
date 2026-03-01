package com.sakti.erp.repository;

import com.sakti.erp.model.ProductQuickCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductQuickCodeRepository extends JpaRepository<ProductQuickCode, UUID> {
}
