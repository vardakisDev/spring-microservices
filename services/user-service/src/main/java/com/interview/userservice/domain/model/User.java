package com.interview.userservice.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class User {

    private final UUID id;
    private final OffsetDateTime createdAt;
    private String email;
    private String firstName;
    private String lastName;
    private boolean vip;
    private OffsetDateTime updatedAt;

    public User(UUID id,
                String email,
                String firstName,
                String lastName,
                boolean vip,
                OffsetDateTime createdAt,
                OffsetDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = requireText(email, "email must not be blank");
        this.firstName = requireText(firstName, "firstName must not be blank");
        this.lastName = requireText(lastName, "lastName must not be blank");
        this.vip = vip;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public UUID id() { return id; }
    public String email() { return email; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public boolean vip() { return vip; }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updatedAt; }

    public void update(String email, String firstName, String lastName, boolean vip, OffsetDateTime updatedAt) {
        this.email = requireText(email, "email must not be blank");
        this.firstName = requireText(firstName, "firstName must not be blank");
        this.lastName = requireText(lastName, "lastName must not be blank");
        this.vip = vip;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}