package com.interview.cartservice.domain.service;

import com.interview.cartservice.domain.model.CartItem;
import com.interview.cartservice.domain.model.CartType;
import com.interview.cartservice.domain.model.PricingSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public final class PricingCalculator {

    private static final BigDecimal TWENTY_EURO = new BigDecimal("20.00");
    private static final BigDecimal SEVENTY_EURO = new BigDecimal("70.00");
    private static final BigDecimal TWENTY_PERCENT = new BigDecimal("0.20");

    public PricingSummary calculate(CartType cartType, Collection<CartItem> items) {
        Objects.requireNonNull(cartType, "cartType must not be null");
        Objects.requireNonNull(items, "items must not be null");

        BigDecimal subtotal = normalize(items.stream()
                .map(CartItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        int totalQuantity = items.stream()
                .mapToInt(CartItem::quantity)
                .sum();

        BigDecimal discount = calculateDiscount(cartType, items, subtotal, totalQuantity);
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        BigDecimal totalAmount = normalize(subtotal.subtract(discount));
        return new PricingSummary(subtotal, discount, totalAmount, totalQuantity);
    }

    private BigDecimal calculateDiscount(CartType cartType,
                                         Collection<CartItem> items,
                                         BigDecimal subtotal,
                                         int totalQuantity) {
        if (totalQuantity == 5) {
            return normalize(subtotal.multiply(TWENTY_PERCENT));
        }

        if (totalQuantity > 10) {
            if (cartType == CartType.NORMAL) {
                return TWENTY_EURO;
            }

            BigDecimal cheapestUnitPrice = items.stream()
                    .map(CartItem::unitPrice)
                    .min(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);

            return normalize(cheapestUnitPrice.add(SEVENTY_EURO));
        }

        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}