package com.interview.cartservice.application.port.out;

import com.interview.cartservice.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {

    Optional<Cart> findById(UUID cartId);

    Cart save(Cart cart);

    void deleteById(UUID cartId);
}