package com.interview.cartservice.infrastructure.config;

import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.application.port.out.ProductCatalogPort;
import com.interview.cartservice.application.port.out.PurchaseRepositoryPort;
import com.interview.cartservice.application.port.out.UserDirectoryPort;
import com.interview.cartservice.application.usecase.AddCartItemUseCase;
import com.interview.cartservice.application.usecase.CheckoutCartUseCase;
import com.interview.cartservice.application.usecase.CreateCartUseCase;
import com.interview.cartservice.application.usecase.DeleteCartUseCase;
import com.interview.cartservice.application.usecase.GetCartUseCase;
import com.interview.cartservice.application.usecase.GetTopPurchasedProductsUseCase;
import com.interview.cartservice.application.usecase.RemoveCartItemUseCase;
import com.interview.cartservice.domain.service.PricingCalculator;
import com.interview.cartservice.infrastructure.client.ProductServiceHttpClientAdapter;
import com.interview.cartservice.infrastructure.client.UserServiceHttpClientAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Clock;

@Configuration
public class CartServiceConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    PricingCalculator pricingCalculator() {
        return new PricingCalculator();
    }

    @Bean
    RestClient userServiceRestClient(@Value("${clients.user-service.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    RestClient productServiceRestClient(@Value("${clients.product-service.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    UserDirectoryPort userDirectoryPort(@Qualifier("userServiceRestClient") RestClient userServiceRestClient) {
        return new UserServiceHttpClientAdapter(userServiceRestClient);
    }

    @Bean
    ProductCatalogPort productCatalogPort(@Qualifier("productServiceRestClient") RestClient productServiceRestClient) {
        return new ProductServiceHttpClientAdapter(productServiceRestClient);
    }

    @Bean
    CreateCartUseCase createCartUseCase(CartRepositoryPort cartRepositoryPort,
                                        UserDirectoryPort userDirectoryPort,
                                        PricingCalculator pricingCalculator,
                                        Clock clock) {
        return new CreateCartUseCase(cartRepositoryPort, userDirectoryPort, pricingCalculator, clock);
    }

    @Bean
    AddCartItemUseCase addCartItemUseCase(CartRepositoryPort cartRepositoryPort, ProductCatalogPort productCatalogPort) {
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
                                            PurchaseRepositoryPort purchaseRepositoryPort,
                                            Clock clock) {
        return new CheckoutCartUseCase(cartRepositoryPort, purchaseRepositoryPort, clock);
    }

    @Bean
    GetTopPurchasedProductsUseCase getTopPurchasedProductsUseCase(UserDirectoryPort userDirectoryPort,
                                                                  PurchaseRepositoryPort purchaseRepositoryPort) {
        return new GetTopPurchasedProductsUseCase(userDirectoryPort, purchaseRepositoryPort);
    }
}