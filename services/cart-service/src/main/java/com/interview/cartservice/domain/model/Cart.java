package com.interview.cartservice.domain.model;

import com.interview.cartservice.domain.service.PricingCalculator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Cart {

    private final UUID id;
    private final UUID userId;
    private final CartType cartType;
    private final OffsetDateTime createdAt;
    private final PricingCalculator pricingCalculator;
    private final Map<UUID, CartItem> items;

    private CartStatus status;
    private OffsetDateTime updatedAt;

    public Cart(UUID id, UUID userId, CartType cartType, OffsetDateTime createdAt, PricingCalculator pricingCalculator) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.cartType = Objects.requireNonNull(cartType, "cartType must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = createdAt;
        this.pricingCalculator = Objects.requireNonNull(pricingCalculator, "pricingCalculator must not be null");
        this.status = CartStatus.OPEN;
        this.items = new LinkedHashMap<>();
    }

    public static Cart rehydrate(UUID id,
                                 UUID userId,
                                 CartType cartType,
                                 CartStatus status,
                                 OffsetDateTime createdAt,
                                 OffsetDateTime updatedAt,
                                 List<CartItem> items,
                                 PricingCalculator pricingCalculator) {
        Cart cart = new Cart(id, userId, cartType, createdAt, pricingCalculator);
        cart.status = Objects.requireNonNull(status, "status must not be null");
        cart.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        Objects.requireNonNull(items, "items must not be null")
                .forEach(item -> cart.items.put(item.productId(), item.snapshot()));
        return cart;
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public CartType cartType() {
        return cartType;
    }

    public CartStatus status() {
        return status;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime updatedAt() {
        return updatedAt;
    }

    public List<CartItem> items() {
        return items.values().stream()
                .map(CartItem::snapshot)
                .toList();
    }

    public void addItem(UUID productId, String productName, BigDecimal unitPrice, int quantity) {
        ensureOpen();

        CartItem existingItem = items.get(productId);
        if (existingItem == null) {
            items.put(productId, new CartItem(productId, productName, unitPrice, quantity));
        } else {
            existingItem.increaseQuantity(quantity);
        }

        touch();
    }

    public void removeItem(UUID productId, int quantity) {
        ensureOpen();

        CartItem existingItem = items.get(productId);
        if (existingItem == null) {
            throw new IllegalArgumentException("product is not present in the cart");
        }

        existingItem.decreaseQuantity(quantity);
        if (existingItem.quantity() == 0) {
            items.remove(productId);
        }

        touch();
    }

    public PricingSummary pricingSummary() {
        return pricingCalculator.calculate(cartType, items.values());
    }

    public Purchase checkout(UUID purchaseId, OffsetDateTime checkedOutAt) {
        ensureOpen();
        if (items.isEmpty()) {
            throw new IllegalStateException("cannot checkout an empty cart");
        }

        PricingSummary pricingSummary = pricingSummary();
        List<PurchaseItem> purchaseItems = items.values().stream()
                .map(item -> new PurchaseItem(
                        item.productId(),
                        item.productName(),
                        item.unitPrice(),
                        item.quantity(),
                        item.lineTotal(),
                        checkedOutAt))
                .sorted(Comparator.comparing(PurchaseItem::productName))
                .toList();

        status = CartStatus.CHECKED_OUT;
        updatedAt = checkedOutAt;

        return new Purchase(
                Objects.requireNonNull(purchaseId, "purchaseId must not be null"),
                id,
                userId,
                cartType,
                pricingSummary.totalAmount(),
                checkedOutAt,
                purchaseItems);
    }

    private void ensureOpen() {
        if (status != CartStatus.OPEN) {
            throw new IllegalStateException("cart is not open");
        }
    }

    private void touch() {
        updatedAt = OffsetDateTime.now();
    }
}