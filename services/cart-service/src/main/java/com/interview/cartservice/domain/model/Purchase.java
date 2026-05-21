package com.interview.cartservice.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Purchase(
        UUID id,
        UUID cartId,
        UUID userId,
        CartType cartType,
        BigDecimal totalAmount,
        OffsetDateTime checkedOutAt,
        List<PurchaseItem> items) {
}