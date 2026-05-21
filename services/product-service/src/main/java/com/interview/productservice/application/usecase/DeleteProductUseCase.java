package com.interview.productservice.application.usecase;

import com.interview.productservice.application.exception.ResourceNotFoundException;
import com.interview.productservice.application.port.out.ProductRepositoryPort;

import java.util.Objects;
import java.util.UUID;

public class DeleteProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public DeleteProductUseCase(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = Objects.requireNonNull(productRepositoryPort, "productRepositoryPort must not be null");
    }

    public void execute(UUID productId) {
        if (productRepositoryPort.findById(productId).isEmpty()) {
            throw new ResourceNotFoundException("product not found: " + productId);
        }
        productRepositoryPort.deleteById(productId);
    }
}