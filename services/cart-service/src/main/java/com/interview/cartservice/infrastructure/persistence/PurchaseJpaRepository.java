package com.interview.cartservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseJpaRepository extends JpaRepository<PurchaseEntity, UUID> {

    List<PurchaseEntity> findByUserId(UUID userId);
}