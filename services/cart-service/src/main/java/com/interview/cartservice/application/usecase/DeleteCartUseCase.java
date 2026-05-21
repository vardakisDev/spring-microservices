package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.exception.ConflictException;
import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.domain.model.Cart;
import com.interview.cartservice.domain.model.CartStatus;

import java.util.Objects;
import java.util.UUID;

public class DeleteCartUseCase {

    private final CartRepositoryPort cartRepositoryPort;

    public DeleteCartUseCase(CartRepositoryPort cartRepositoryPort) {
        this.cartRepositoryPort = Objects.requireNonNull(cartRepositoryPort, "cartRepositoryPort must not be null");
    }

    public void execute(UUID cartId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("cart not found: " + cartId));

        if (cart.status() != CartStatus.OPEN) {
            throw new ConflictException("checked out carts cannot be deleted");
        }

        cartRepositoryPort.deleteById(cartId);
    }
}