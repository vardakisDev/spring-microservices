package com.interview.productservice.infrastructure.web;

import com.interview.commonsecurity.CommonLocalJwtAutoConfiguration;
import com.interview.commonsecurity.CommonSecurityAutoConfiguration;
import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.application.usecase.CreateProductUseCase;
import com.interview.productservice.application.usecase.DeleteProductUseCase;
import com.interview.productservice.application.usecase.GetProductUseCase;
import com.interview.productservice.application.usecase.ListProductsUseCase;
import com.interview.productservice.application.usecase.UpdateProductUseCase;
import com.interview.productservice.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({CommonSecurityAutoConfiguration.class, CommonLocalJwtAutoConfiguration.class, ProductSecurityTest.StubUseCaseConfiguration.class})
class ProductSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void rejectsAnonymousRequest() throws Exception {
        mockMvc.perform(delete("/products/{productId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void acceptsValidBearerToken() throws Exception {
        mockMvc.perform(delete("/products/{productId}", UUID.randomUUID())
                        .header(AUTHORIZATION, "Bearer " + tokenForAudience("product-service")))
                .andExpect(status().isNoContent());
    }

    private String tokenForAudience(String audience) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("shopping-platform-local")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(300))
                .subject("test-client")
                .audience(List.of(audience))
                .claim("scope", "test")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @TestConfiguration
    static class StubUseCaseConfiguration {

        @Bean
        CreateProductUseCase createProductUseCase(ProductRepositoryPort productRepositoryPort) {
            return new CreateProductUseCase(productRepositoryPort, Clock.systemUTC());
        }

        @Bean
        GetProductUseCase getProductUseCase(ProductRepositoryPort productRepositoryPort) {
            return new GetProductUseCase(productRepositoryPort);
        }

        @Bean
        ListProductsUseCase listProductsUseCase(ProductRepositoryPort productRepositoryPort) {
            return new ListProductsUseCase(productRepositoryPort);
        }

        @Bean
        UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepositoryPort) {
            return new UpdateProductUseCase(productRepositoryPort, Clock.systemUTC());
        }

        @Bean
        DeleteProductUseCase deleteProductUseCase(ProductRepositoryPort productRepositoryPort) {
            return new DeleteProductUseCase(productRepositoryPort);
        }

        @Bean
        ProductRepositoryPort productRepositoryPort() {
            return new ProductRepositoryPort() {
                @Override
                public Optional<Product> findById(UUID productId) {
                    OffsetDateTime now = OffsetDateTime.now();
                    return Optional.of(new Product(productId, "Laptop", "Main product", new BigDecimal("10.00"), now, now));
                }

                @Override
                public Optional<Product> findByName(String name) {
                    return Optional.empty();
                }

                @Override
                public List<Product> findAll() {
                    OffsetDateTime now = OffsetDateTime.now();
                    return List.of(new Product(UUID.randomUUID(), "Laptop", "Main product", new BigDecimal("10.00"), now, now));
                }

                @Override
                public Product save(Product product) {
                    return product;
                }

                @Override
                public void deleteById(UUID productId) {
                }
            };
        }
    }
}