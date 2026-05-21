package com.interview.cartservice.domain.model;

import com.interview.cartservice.domain.service.PricingCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {

    private final PricingCalculator pricingCalculator = new PricingCalculator();

    @Test
    void aggregatesQuantitiesWhenAddingTheSameProduct() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), CartType.NORMAL, OffsetDateTime.now(), pricingCalculator);
        UUID productId = UUID.randomUUID();

        cart.addItem(productId, "Apple", new BigDecimal("10.00"), 2);
        cart.addItem(productId, "Apple", new BigDecimal("10.00"), 3);

        assertThat(cart.items()).hasSize(1);
        assertThat(cart.items().getFirst().quantity()).isEqualTo(5);
        assertThat(cart.pricingSummary().totalAmount()).isEqualByComparingTo("40.00");
    }

    @Test
    void removesLineWhenQuantityFallsToZero() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), CartType.NORMAL, OffsetDateTime.now(), pricingCalculator);
        UUID productId = UUID.randomUUID();

        cart.addItem(productId, "Orange", new BigDecimal("5.00"), 2);
        cart.removeItem(productId, 2);

        assertThat(cart.items()).isEmpty();
    }

    @Test
    void checkoutReturnsPurchaseSnapshotAndClosesCart() {
        OffsetDateTime createdAt = OffsetDateTime.now();
        OffsetDateTime checkedOutAt = createdAt.plusMinutes(15);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), CartType.VIP, createdAt, pricingCalculator);

        cart.addItem(UUID.randomUUID(), "Laptop", new BigDecimal("100.00"), 8);
        cart.addItem(UUID.randomUUID(), "Mouse", new BigDecimal("10.00"), 3);

        Purchase purchase = cart.checkout(UUID.randomUUID(), checkedOutAt);

        assertThat(cart.status()).isEqualTo(CartStatus.CHECKED_OUT);
        assertThat(purchase.cartType()).isEqualTo(CartType.VIP);
        assertThat(purchase.totalAmount()).isEqualByComparingTo("750.00");
        assertThat(purchase.items()).hasSize(2);
        assertThat(purchase.checkedOutAt()).isEqualTo(checkedOutAt);
    }

    @Test
    void forbidsMutatingCheckedOutCart() {
        OffsetDateTime createdAt = OffsetDateTime.now();
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), CartType.NORMAL, createdAt, pricingCalculator);

        cart.addItem(UUID.randomUUID(), "Book", new BigDecimal("15.00"), 1);
        cart.checkout(UUID.randomUUID(), createdAt.plusMinutes(5));

        assertThatThrownBy(() -> cart.addItem(UUID.randomUUID(), "Pen", new BigDecimal("2.00"), 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("cart is not open");
    }

    @Test
    void forbidsCheckoutOfEmptyCart() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), CartType.NORMAL, OffsetDateTime.now(), pricingCalculator);

        assertThatThrownBy(() -> cart.checkout(UUID.randomUUID(), OffsetDateTime.now().plusMinutes(1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("cannot checkout an empty cart");
    }
}