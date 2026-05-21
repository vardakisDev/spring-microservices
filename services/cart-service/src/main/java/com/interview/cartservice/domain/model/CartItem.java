package com.interview.cartservice.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public final class CartItem {

    private final UUID productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private int quantity;

    public CartItem(UUID productId, String productName, BigDecimal unitPrice, int quantity) {
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.productName = requireText(productName, "productName must not be blank");
        this.unitPrice = normalizeMoney(requirePositive(unitPrice, "unitPrice must be positive"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        this.quantity = quantity;
    }

    public UUID productId() {
        return productId;
    }

    public String productName() {
        return productName;
    }

    public BigDecimal unitPrice() {
        return unitPrice;
    }

    public int quantity() {
        return quantity;
    }

    public BigDecimal lineTotal() {
        return normalizeMoney(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("quantity increment must be positive");
        }

        quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("quantity decrement must be positive");
        }

        if (amount > quantity) {
            throw new IllegalArgumentException("cannot remove more items than are present");
        }

        quantity -= amount;
    }

    public CartItem snapshot() {
        return new CartItem(productId, productName, unitPrice, quantity);
    }

    private static BigDecimal requirePositive(BigDecimal amount, String message) {
        Objects.requireNonNull(amount, message);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
        return amount;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static BigDecimal normalizeMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}