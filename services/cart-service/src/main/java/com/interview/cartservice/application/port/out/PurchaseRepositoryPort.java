package com.interview.cartservice.application.port.out;

import com.interview.cartservice.domain.model.Purchase;

import java.util.List;
import java.util.UUID;

public interface PurchaseRepositoryPort {

    Purchase save(Purchase purchase);

    List<Purchase> findByUserId(UUID userId);
}