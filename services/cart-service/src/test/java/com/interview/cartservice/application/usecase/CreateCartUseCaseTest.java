package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.exception.ConflictException;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.application.port.out.UserDirectoryPort;
import com.interview.cartservice.application.usecase.command.CreateCartCommand;
import com.interview.cartservice.domain.model.Cart;
import com.interview.cartservice.domain.model.CartType;
import com.interview.cartservice.domain.service.PricingCalculator;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCartUseCaseTest {

    private final InMemoryCartRepository cartRepository = new InMemoryCartRepository();
    private final Clock clock = Clock.fixed(Instant.parse("2026-05-19T12:00:00Z"), ZoneOffset.UTC);
    private final PricingCalculator pricingCalculator = new PricingCalculator();

    @Test
    void createsVipCartWhenUserIsEligible() {
        UUID userId = UUID.randomUUID();
        CreateCartUseCase useCase = new CreateCartUseCase(
                cartRepository,
                ignored -> new UserDirectoryPort.UserSnapshot(userId, true),
                pricingCalculator,
                clock);

        Cart cart = useCase.execute(new CreateCartCommand(userId, CartType.VIP));

        assertThat(cart.userId()).isEqualTo(userId);
        assertThat(cart.cartType()).isEqualTo(CartType.VIP);
        assertThat(cartRepository.findById(cart.id())).contains(cart);
    }

    @Test
    void rejectsVipCartWhenUserIsNotEligible() {
        UUID userId = UUID.randomUUID();
        CreateCartUseCase useCase = new CreateCartUseCase(
                cartRepository,
                ignored -> new UserDirectoryPort.UserSnapshot(userId, false),
                pricingCalculator,
                clock);
        CreateCartCommand command = new CreateCartCommand(userId, CartType.VIP);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ConflictException.class)
                .hasMessage("user is not eligible for VIP carts");
    }

    private static final class InMemoryCartRepository implements CartRepositoryPort {

        private final Map<UUID, Cart> carts = new HashMap<>();

        @Override
        public Optional<Cart> findById(UUID cartId) {
            return Optional.ofNullable(carts.get(cartId));
        }

        @Override
        public Cart save(Cart cart) {
            carts.put(cart.id(), cart);
            return cart;
        }

        @Override
        public void deleteById(UUID cartId) {
            carts.remove(cartId);
        }
    }
}