package com.interview.cartservice.infrastructure.client;

import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.cartservice.application.port.out.ProductCatalogPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductServiceHttpClientAdapter implements ProductCatalogPort {

    private final RestClient restClient;

    public ProductServiceHttpClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ProductSnapshot getProduct(UUID productId) {
        try {
            ProductResponse response = restClient.get()
                    .uri("/products/{productId}", productId)
                    .retrieve()
                    .body(ProductResponse.class);

            if (response == null || response.id() == null) {
                throw new ResourceNotFoundException("product not found: " + productId);
            }

            return new ProductSnapshot(response.id(), response.name(), response.price());
        } catch (RestClientResponseException exception) {
            if (HttpStatusCode.valueOf(exception.getStatusCode().value()).value() == 404) {
                throw new ResourceNotFoundException("product not found: " + productId);
            }
            throw exception;
        }
    }

    private record ProductResponse(UUID id, String name, BigDecimal price) {
    }
}