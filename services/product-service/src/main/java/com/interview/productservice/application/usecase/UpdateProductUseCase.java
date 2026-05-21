package com.interview.productservice.application.usecase;

import com.interview.productservice.application.exception.ConflictException;
import com.interview.productservice.application.exception.ResourceNotFoundException;
import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.application.usecase.command.UpdateProductCommand;
import com.interview.productservice.domain.model.Product;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class UpdateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final Clock clock;

    public UpdateProductUseCase(ProductRepositoryPort productRepositoryPort, Clock clock) {
        this.productRepositoryPort = Objects.requireNonNull(productRepositoryPort, "productRepositoryPort must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Product execute(UUID productId, UpdateProductCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product not found: " + productId));

        productRepositoryPort.findByName(command.name())
                .filter(existing -> !existing.id().equals(productId))
                .ifPresent(existing -> {
                    throw new ConflictException("product already exists: " + command.name());
                });

        product.update(command.name(), command.description(), command.price(), OffsetDateTime.now(clock));
        return productRepositoryPort.save(product);
    }
}