package com.interview.productservice.application.usecase;

import com.interview.productservice.application.exception.ResourceNotFoundException;
import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.domain.model.Product;

import java.util.Objects;
import java.util.UUID;

public class GetProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public GetProductUseCase(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = Objects.requireNonNull(productRepositoryPort, "productRepositoryPort must not be null");
    }

    public Product execute(UUID productId) {
        return productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product not found: " + productId));
    }
}