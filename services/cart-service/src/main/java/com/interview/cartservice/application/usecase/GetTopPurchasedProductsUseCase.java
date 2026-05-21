package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.port.out.PurchaseRepositoryPort;
import com.interview.cartservice.application.port.out.UserDirectoryPort;
import com.interview.cartservice.application.usecase.query.PurchasedProductView;
import com.interview.cartservice.domain.model.PurchaseItem;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GetTopPurchasedProductsUseCase {

    private final UserDirectoryPort userDirectoryPort;
    private final PurchaseRepositoryPort purchaseRepositoryPort;

    public GetTopPurchasedProductsUseCase(UserDirectoryPort userDirectoryPort, PurchaseRepositoryPort purchaseRepositoryPort) {
        this.userDirectoryPort = Objects.requireNonNull(userDirectoryPort, "userDirectoryPort must not be null");
        this.purchaseRepositoryPort = Objects.requireNonNull(purchaseRepositoryPort, "purchaseRepositoryPort must not be null");
    }

    public List<PurchasedProductView> execute(UUID userId) {
        userDirectoryPort.getUser(userId);

        Map<UUID, ProductAccumulator> productsById = new HashMap<>();
        purchaseRepositoryPort.findByUserId(userId).stream()
                .flatMap(purchase -> purchase.items().stream())
                .forEach(item -> productsById.compute(item.productId(), (ignored, existing) ->
                        existing == null ? ProductAccumulator.from(item) : existing.merge(item)));

        return productsById.values().stream()
                .map(ProductAccumulator::toView)
                .sorted(Comparator
                        .comparing(PurchasedProductView::unitPrice).reversed()
                        .thenComparing(PurchasedProductView::productName))
                .limit(4)
                .toList();
    }

    private record ProductAccumulator(UUID productId, String productName, BigDecimal highestUnitPrice, long purchaseCount) {

        static ProductAccumulator from(PurchaseItem item) {
            return new ProductAccumulator(item.productId(), item.productName(), item.unitPrice(), 1L);
        }

        ProductAccumulator merge(PurchaseItem item) {
            BigDecimal highestPrice = highestUnitPrice.max(item.unitPrice());
            String resolvedName = highestPrice.compareTo(highestUnitPrice) > 0 ? item.productName() : productName;
            return new ProductAccumulator(productId, resolvedName, highestPrice, purchaseCount + 1);
        }

        PurchasedProductView toView() {
            return new PurchasedProductView(productId, productName, highestUnitPrice, purchaseCount);
        }
    }
}