package com.interview.cartservice.domain.service;

import com.interview.cartservice.domain.model.CartItem;
import com.interview.cartservice.domain.model.CartType;
import com.interview.cartservice.domain.model.PricingSummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PricingCalculatorTest {

    private final PricingCalculator pricingCalculator = new PricingCalculator();

    @Test
    void appliesTwentyPercentDiscountWhenCartContainsExactlyFiveItems() {
        PricingSummary summary = pricingCalculator.calculate(CartType.NORMAL, List.of(
                item("Apple", "10.00", 2),
                item("Orange", "5.00", 3)));

        assertThat(summary.subtotal()).isEqualByComparingTo("35.00");
        assertThat(summary.discountAmount()).isEqualByComparingTo("7.00");
        assertThat(summary.totalAmount()).isEqualByComparingTo("28.00");
        assertThat(summary.totalQuantity()).isEqualTo(5);
    }

    @Test
    void appliesFixedTwentyEuroDiscountForNormalCartAboveTenItems() {
        PricingSummary summary = pricingCalculator.calculate(CartType.NORMAL, List.of(
                item("Apple", "10.00", 6),
                item("Orange", "5.00", 5)));

        assertThat(summary.subtotal()).isEqualByComparingTo("85.00");
        assertThat(summary.discountAmount()).isEqualByComparingTo("20.00");
        assertThat(summary.totalAmount()).isEqualByComparingTo("65.00");
        assertThat(summary.totalQuantity()).isEqualTo(11);
    }

    @Test
    void appliesVipDiscountWithCheapestItemFreeAndAdditionalSeventyEuroDiscount() {
        PricingSummary summary = pricingCalculator.calculate(CartType.VIP, List.of(
                item("Apple", "10.00", 8),
                item("Orange", "5.00", 3)));

        assertThat(summary.subtotal()).isEqualByComparingTo("95.00");
        assertThat(summary.discountAmount()).isEqualByComparingTo("75.00");
        assertThat(summary.totalAmount()).isEqualByComparingTo("20.00");
        assertThat(summary.totalQuantity()).isEqualTo(11);
    }

    @Test
    void doesNotAllowDiscountToExceedSubtotal() {
        PricingSummary summary = pricingCalculator.calculate(CartType.VIP, List.of(
                item("Pen", "2.00", 11)));

        assertThat(summary.subtotal()).isEqualByComparingTo("22.00");
        assertThat(summary.discountAmount()).isEqualByComparingTo("22.00");
        assertThat(summary.totalAmount()).isEqualByComparingTo("0.00");
    }

    private CartItem item(String name, String unitPrice, int quantity) {
        return new CartItem(UUID.randomUUID(), name, new BigDecimal(unitPrice), quantity);
    }
}