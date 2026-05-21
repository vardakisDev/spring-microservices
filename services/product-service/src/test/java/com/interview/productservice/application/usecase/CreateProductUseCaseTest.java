package com.interview.productservice.application.usecase;

import com.interview.productservice.application.exception.ConflictException;
import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.application.usecase.command.CreateProductCommand;
import com.interview.productservice.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateProductUseCaseTest {

    private final InMemoryProductRepository productRepository = new InMemoryProductRepository();
    private final Clock clock = Clock.fixed(Instant.parse("2026-05-19T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void createsProductWhenNameIsUnique() {
        CreateProductUseCase useCase = new CreateProductUseCase(productRepository, clock);

        Product product = useCase.execute(new CreateProductCommand("Laptop", "Main product", new BigDecimal("999.99")));

        assertThat(product.name()).isEqualTo("Laptop");
        assertThat(product.price()).isEqualByComparingTo("999.99");
        assertThat(productRepository.findById(product.id())).contains(product);
    }

    @Test
    void rejectsDuplicateNames() {
        CreateProductUseCase useCase = new CreateProductUseCase(productRepository, clock);
        useCase.execute(new CreateProductCommand("Laptop", "Main product", new BigDecimal("999.99")));
        CreateProductCommand duplicateCommand = new CreateProductCommand("Laptop", "Other product", new BigDecimal("1000.00"));

        assertThatThrownBy(() -> useCase.execute(duplicateCommand))
                .isInstanceOf(ConflictException.class)
                .hasMessage("product already exists: Laptop");
    }

    private static final class InMemoryProductRepository implements ProductRepositoryPort {
        private final Map<UUID, Product> products = new HashMap<>();

        @Override public Optional<Product> findById(UUID productId) { return Optional.ofNullable(products.get(productId)); }
        @Override public Optional<Product> findByName(String name) { return products.values().stream().filter(p -> p.name().equalsIgnoreCase(name)).findFirst(); }
        @Override public List<Product> findAll() { return products.values().stream().toList(); }
        @Override public Product save(Product product) { products.put(product.id(), product); return product; }
        @Override public void deleteById(UUID productId) { products.remove(productId); }
    }
}