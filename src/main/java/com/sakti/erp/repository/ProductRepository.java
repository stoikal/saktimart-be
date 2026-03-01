package com.sakti.erp.repository;

import com.sakti.erp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByBarcode(String barcode);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.prices LEFT JOIN p.quickCodes qc WHERE p.barcode = :code OR LOWER(qc.code) = LOWER(:code)")
    Optional<Product> findByBarcodeOrQuickCode(@Param("code") String code);
}
