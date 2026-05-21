package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.application.port.out.PurchaseRepositoryPort;
import com.interview.cartservice.domain.model.Cart;
import com.interview.cartservice.domain.model.Purchase;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class CheckoutCartUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final PurchaseRepositoryPort purchaseRepositoryPort;
    private final Clock clock;

    public CheckoutCartUseCase(CartRepositoryPort cartRepositoryPort,
                               PurchaseRepositoryPort purchaseRepositoryPort,
                               Clock clock) {
        this.cartRepositoryPort = Objects.requireNonNull(cartRepositoryPort, "cartRepositoryPort must not be null");
        this.purchaseRepositoryPort = Objects.requireNonNull(purchaseRepositoryPort, "purchaseRepositoryPort must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Purchase execute(UUID cartId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("cart not found: " + cartId));

        Purchase purchase = cart.checkout(UUID.randomUUID(), OffsetDateTime.now(clock));
        cartRepositoryPort.save(cart);
        return purchaseRepositoryPort.save(purchase);
    }
}