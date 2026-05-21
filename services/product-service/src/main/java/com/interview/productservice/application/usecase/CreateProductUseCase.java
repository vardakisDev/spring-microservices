package com.interview.productservice.application.usecase;

import com.interview.productservice.application.exception.ConflictException;
import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.application.usecase.command.CreateProductCommand;
import com.interview.productservice.domain.model.Product;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final Clock clock;

    public CreateProductUseCase(ProductRepositoryPort productRepositoryPort, Clock clock) {
        this.productRepositoryPort = Objects.requireNonNull(productRepositoryPort, "productRepositoryPort must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Product execute(CreateProductCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        productRepositoryPort.findByName(command.name()).ifPresent(existing -> {
            throw new ConflictException("product already exists: " + command.name());
        });

        OffsetDateTime now = OffsetDateTime.now(clock);
        Product product = new Product(UUID.randomUUID(), command.name(), command.description(), command.price(), now, now);
        return productRepositoryPort.save(product);
    }
}