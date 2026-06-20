package com.stoikal.saktimart.customer.repository;

import com.stoikal.saktimart.customer.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    @Query("SELECT c FROM CustomerEntity c WHERE c.deletedAt IS NULL")
    Page<CustomerEntity> findAllActive(Pageable pageable);

    @Query("SELECT c FROM CustomerEntity c WHERE c.idCustomer = ?1 AND c.deletedAt IS NULL")
    Optional<CustomerEntity> findActiveById(UUID id);
}
