package com.interview.productservice.infrastructure.persistence;

import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.domain.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;

    public JpaProductRepositoryAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return productJpaRepository.findById(productId).map(this::toDomain);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return productJpaRepository.findByNameIgnoreCase(name).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(ProductEntity::getCreatedAt))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Product save(Product product) {
        return toDomain(productJpaRepository.save(toEntity(product)));
    }

    @Override
    public void deleteById(UUID productId) {
        productJpaRepository.deleteById(productId);
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.id());
        entity.setName(product.name());
        entity.setDescription(product.description());
        entity.setPrice(product.price());
        entity.setCreatedAt(product.createdAt());
        entity.setUpdatedAt(product.updatedAt());
        return entity;
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}