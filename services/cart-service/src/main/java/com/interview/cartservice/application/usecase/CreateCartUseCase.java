package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.exception.ConflictException;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.application.port.out.UserDirectoryPort;
import com.interview.cartservice.application.usecase.command.CreateCartCommand;
import com.interview.cartservice.domain.model.Cart;
import com.interview.cartservice.domain.model.CartType;
import com.interview.cartservice.domain.service.PricingCalculator;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class CreateCartUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final UserDirectoryPort userDirectoryPort;
    private final PricingCalculator pricingCalculator;
    private final Clock clock;

    public CreateCartUseCase(CartRepositoryPort cartRepositoryPort,
                             UserDirectoryPort userDirectoryPort,
                             PricingCalculator pricingCalculator,
                             Clock clock) {
        this.cartRepositoryPort = Objects.requireNonNull(cartRepositoryPort, "cartRepositoryPort must not be null");
        this.userDirectoryPort = Objects.requireNonNull(userDirectoryPort, "userDirectoryPort must not be null");
        this.pricingCalculator = Objects.requireNonNull(pricingCalculator, "pricingCalculator must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Cart execute(CreateCartCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        UserDirectoryPort.UserSnapshot user = userDirectoryPort.getUser(command.userId());
        if (command.cartType() == CartType.VIP && !user.vip()) {
            throw new ConflictException("user is not eligible for VIP carts");
        }

        Cart cart = new Cart(
                UUID.randomUUID(),
                user.id(),
                command.cartType(),
                OffsetDateTime.now(clock),
                pricingCalculator);

        return cartRepositoryPort.save(cart);
    }
}