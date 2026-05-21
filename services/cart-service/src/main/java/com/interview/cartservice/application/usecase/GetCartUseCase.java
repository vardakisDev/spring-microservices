package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.domain.model.Cart;

import java.util.Objects;
import java.util.UUID;

public class GetCartUseCase {

    private final CartRepositoryPort cartRepositoryPort;

    public GetCartUseCase(CartRepositoryPort cartRepositoryPort) {
        this.cartRepositoryPort = Objects.requireNonNull(cartRepositoryPort, "cartRepositoryPort must not be null");
    }

    public Cart execute(UUID cartId) {
        return cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("cart not found: " + cartId));
    }
}