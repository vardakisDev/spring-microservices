package com.interview.productservice.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Product {

    private final UUID id;
    private final OffsetDateTime createdAt;
    private String name;
    private String description;
    private BigDecimal price;
    private OffsetDateTime updatedAt;

    public Product(UUID id,
                   String name,
                   String description,
                   BigDecimal price,
                   OffsetDateTime createdAt,
                   OffsetDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = requireText(name, "name must not be blank");
        this.description = requireText(description, "description must not be blank");
        this.price = normalize(requirePositive(price, "price must be positive"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public BigDecimal price() { return price; }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updatedAt; }

    public void update(String name, String description, BigDecimal price, OffsetDateTime updatedAt) {
        this.name = requireText(name, "name must not be blank");
        this.description = requireText(description, "description must not be blank");
        this.price = normalize(requirePositive(price, "price must be positive"));
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static BigDecimal requirePositive(BigDecimal value, String message) {
        Objects.requireNonNull(value, message);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}