package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.application.usecase.command.RemoveCartItemCommand;
import com.interview.cartservice.domain.model.Cart;

import java.util.Objects;

public class RemoveCartItemUseCase {

    private final CartRepositoryPort cartRepositoryPort;

    public RemoveCartItemUseCase(CartRepositoryPort cartRepositoryPort) {
        this.cartRepositoryPort = Objects.requireNonNull(cartRepositoryPort, "cartRepositoryPort must not be null");
    }

    public Cart execute(RemoveCartItemCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Cart cart = cartRepositoryPort.findById(command.cartId())
                .orElseThrow(() -> new ResourceNotFoundException("cart not found: " + command.cartId()));

        cart.removeItem(command.productId(), command.quantity());
        return cartRepositoryPort.save(cart);
    }
}