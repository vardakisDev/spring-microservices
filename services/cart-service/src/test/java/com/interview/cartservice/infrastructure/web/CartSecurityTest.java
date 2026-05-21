package com.interview.cartservice.infrastructure.web;

import com.interview.commonsecurity.CommonSecurityAutoConfiguration;
import com.interview.commonsecurity.CommonLocalJwtAutoConfiguration;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.application.port.out.ProductCatalogPort;
import com.interview.cartservice.application.port.out.PurchaseRepositoryPort;
import com.interview.cartservice.application.port.out.UserDirectoryPort;
import com.interview.cartservice.application.usecase.AddCartItemUseCase;
import com.interview.cartservice.application.usecase.CheckoutCartUseCase;
import com.interview.cartservice.application.usecase.CreateCartUseCase;
import com.interview.cartservice.application.usecase.DeleteCartUseCase;
import com.interview.cartservice.application.usecase.GetCartUseCase;
import com.interview.cartservice.application.usecase.RemoveCartItemUseCase;
import com.interview.cartservice.domain.model.Cart;
import com.interview.cartservice.domain.model.CartType;
import com.interview.cartservice.domain.model.Purchase;
import com.interview.cartservice.domain.service.PricingCalculator;
import com.interview.cartservice.infrastructure.config.LocalJwtSecurityConfiguration;
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

@WebMvcTest(CartController.class)
@Import({CommonSecurityAutoConfiguration.class, CommonLocalJwtAutoConfiguration.class, LocalJwtSecurityConfiguration.class, CartSecurityTest.StubUseCaseConfiguration.class})
class CartSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void rejectsAnonymousRequest() throws Exception {
        mockMvc.perform(delete("/carts/{cartId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void acceptsValidBearerToken() throws Exception {
        mockMvc.perform(delete("/carts/{cartId}", UUID.randomUUID())
                        .header(AUTHORIZATION, "Bearer " + tokenForAudience("cart-service")))
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
        CreateCartUseCase createCartUseCase(CartRepositoryPort cartRepositoryPort,
                                            UserDirectoryPort userDirectoryPort) {
            return new CreateCartUseCase(cartRepositoryPort, userDirectoryPort, new PricingCalculator(), Clock.systemUTC());
        }

        @Bean
        AddCartItemUseCase addCartItemUseCase(CartRepositoryPort cartRepositoryPort,
                                              ProductCatalogPort productCatalogPort) {
            return new AddCartItemUseCase(cartRepositoryPort, productCatalogPort);
        }

        @Bean
        RemoveCartItemUseCase removeCartItemUseCase(CartRepositoryPort cartRepositoryPort) {
            return new RemoveCartItemUseCase(cartRepositoryPort);
        }

        @Bean
        GetCartUseCase getCartUseCase(CartRepositoryPort cartRepositoryPort) {
            return new GetCartUseCase(cartRepositoryPort);
        }

        @Bean
        DeleteCartUseCase deleteCartUseCase(CartRepositoryPort cartRepositoryPort) {
            return new DeleteCartUseCase(cartRepositoryPort);
        }

        @Bean
        CheckoutCartUseCase checkoutCartUseCase(CartRepositoryPort cartRepositoryPort,
                                                PurchaseRepositoryPort purchaseRepositoryPort) {
            return new CheckoutCartUseCase(cartRepositoryPort, purchaseRepositoryPort, Clock.systemUTC());
        }

        @Bean
        CartRepositoryPort cartRepositoryPort() {
            return new CartRepositoryPort() {
                @Override
                public Optional<Cart> findById(UUID cartId) {
                    return Optional.of(new Cart(cartId, UUID.randomUUID(), CartType.NORMAL, OffsetDateTime.now(), new PricingCalculator()));
                }

                @Override
                public Cart save(Cart cart) {
                    return cart;
                }

                @Override
                public void deleteById(UUID cartId) {
                }
            };
        }

        @Bean
        ProductCatalogPort productCatalogPort() {
            return productId -> new ProductCatalogPort.ProductSnapshot(productId, "Stub product", new BigDecimal("10.00"));
        }

        @Bean
        UserDirectoryPort userDirectoryPort() {
            return userId -> new UserDirectoryPort.UserSnapshot(userId, true);
        }

        @Bean
        PurchaseRepositoryPort purchaseRepositoryPort() {
            return new PurchaseRepositoryPort() {
                @Override
                public Purchase save(Purchase purchase) {
                    return purchase;
                }

                @Override
                public List<Purchase> findByUserId(UUID userId) {
                    return List.of();
                }
            };
        }
    }
}