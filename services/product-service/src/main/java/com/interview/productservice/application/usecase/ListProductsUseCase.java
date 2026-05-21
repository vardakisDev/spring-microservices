package com.interview.productservice.application.usecase;

import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.domain.model.Product;

import java.util.List;
import java.util.Objects;

public class ListProductsUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public ListProductsUseCase(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = Objects.requireNonNull(productRepositoryPort, "productRepositoryPort must not be null");
    }

    public List<Product> execute() {
        return productRepositoryPort.findAll();
    }
}