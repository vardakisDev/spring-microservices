package com.interview.cartservice.application.usecase.query;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchasedProductView(UUID productId, String productName, BigDecimal unitPrice, long purchaseCount) {
}