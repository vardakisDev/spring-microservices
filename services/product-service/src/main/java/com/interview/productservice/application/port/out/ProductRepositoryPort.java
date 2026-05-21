package com.interview.productservice.application.port.out;

import com.interview.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {

    Optional<Product> findById(UUID productId);

    Optional<Product> findByName(String name);

    List<Product> findAll();

    Product save(Product product);

    void deleteById(UUID productId);
}