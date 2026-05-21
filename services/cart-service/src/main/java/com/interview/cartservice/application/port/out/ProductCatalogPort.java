package com.interview.cartservice.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductCatalogPort {

    ProductSnapshot getProduct(UUID productId);

    record ProductSnapshot(UUID id, String name, BigDecimal unitPrice) {
    }
}