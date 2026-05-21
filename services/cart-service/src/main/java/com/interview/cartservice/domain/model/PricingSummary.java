package com.interview.cartservice.domain.model;

import java.math.BigDecimal;

public record PricingSummary(
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        int totalQuantity) {
}