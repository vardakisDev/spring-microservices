package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.port.out.PurchaseRepositoryPort;
import com.interview.cartservice.application.port.out.UserDirectoryPort;
import com.interview.cartservice.application.usecase.query.PurchasedProductView;
import com.interview.cartservice.domain.model.CartType;
import com.interview.cartservice.domain.model.Purchase;
import com.interview.cartservice.domain.model.PurchaseItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetTopPurchasedProductsUseCaseTest {

    @Test
    void returnsUpToFourDistinctProductsSortedByHighestHistoricalPrice() {
        UUID userId = UUID.randomUUID();
        GetTopPurchasedProductsUseCase useCase = new GetTopPurchasedProductsUseCase(
                ignored -> new UserDirectoryPort.UserSnapshot(userId, true),
            new StubPurchaseRepository(List.of(
                purchase(userId, item("Book", "20.00"), item("Laptop", "900.00")),
                purchase(userId, item("Laptop", "950.00"), item("Headphones", "120.00")),
                purchase(userId, item("Pen", "2.00"), item("Desk", "250.00")),
                purchase(userId, item("Mouse", "40.00")))));

        List<PurchasedProductView> products = useCase.execute(userId);

        assertThat(products).hasSize(4);
        assertThat(products).extracting(PurchasedProductView::productName)
                .containsExactly("Laptop", "Desk", "Headphones", "Mouse");
        assertThat(products.getFirst().unitPrice()).isEqualByComparingTo("950.00");
        assertThat(products.getFirst().purchaseCount()).isEqualTo(2);
    }

    private Purchase purchase(UUID userId, PurchaseItem... items) {
        return new Purchase(
                UUID.randomUUID(),
                UUID.randomUUID(),
                userId,
                CartType.NORMAL,
                BigDecimal.ZERO,
                OffsetDateTime.now(),
                List.of(items));
    }

    private PurchaseItem item(String name, String unitPrice) {
        return new PurchaseItem(
                UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)),
                name,
                new BigDecimal(unitPrice),
                1,
                new BigDecimal(unitPrice),
                OffsetDateTime.now());
    }

    private static final class StubPurchaseRepository implements PurchaseRepositoryPort {

        private final List<Purchase> purchases;

        private StubPurchaseRepository(List<Purchase> purchases) {
            this.purchases = purchases;
        }

        @Override
        public Purchase save(Purchase purchase) {
            return purchase;
        }

        @Override
        public List<Purchase> findByUserId(UUID userId) {
            return purchases;
        }
    }
}