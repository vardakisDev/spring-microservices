package com.interview.productservice.infrastructure.config;

import com.interview.productservice.application.port.out.ProductRepositoryPort;
import com.interview.productservice.application.usecase.CreateProductUseCase;
import com.interview.productservice.application.usecase.DeleteProductUseCase;
import com.interview.productservice.application.usecase.GetProductUseCase;
import com.interview.productservice.application.usecase.ListProductsUseCase;
import com.interview.productservice.application.usecase.UpdateProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ProductServiceConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    CreateProductUseCase createProductUseCase(ProductRepositoryPort productRepositoryPort, Clock clock) {
        return new CreateProductUseCase(productRepositoryPort, clock);
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
    UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepositoryPort, Clock clock) {
        return new UpdateProductUseCase(productRepositoryPort, clock);
    }

    @Bean
    DeleteProductUseCase deleteProductUseCase(ProductRepositoryPort productRepositoryPort) {
        return new DeleteProductUseCase(productRepositoryPort);
    }
}