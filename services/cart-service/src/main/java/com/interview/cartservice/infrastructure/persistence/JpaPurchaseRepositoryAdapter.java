package com.interview.cartservice.infrastructure.persistence;

import com.interview.cartservice.application.port.out.PurchaseRepositoryPort;
import com.interview.cartservice.domain.model.Purchase;
import com.interview.cartservice.domain.model.PurchaseItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaPurchaseRepositoryAdapter implements PurchaseRepositoryPort {

    private final PurchaseJpaRepository purchaseJpaRepository;

    public JpaPurchaseRepositoryAdapter(PurchaseJpaRepository purchaseJpaRepository) {
        this.purchaseJpaRepository = purchaseJpaRepository;
    }

    @Override
    public Purchase save(Purchase purchase) {
        return toDomain(purchaseJpaRepository.save(toEntity(purchase)));
    }

    @Override
    public List<Purchase> findByUserId(java.util.UUID userId) {
        return purchaseJpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    private PurchaseEntity toEntity(Purchase purchase) {
        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(purchase.id());
        entity.setCartId(purchase.cartId());
        entity.setUserId(purchase.userId());
        entity.setCartType(purchase.cartType());
        entity.setTotalAmount(purchase.totalAmount());
        entity.setCheckedOutAt(purchase.checkedOutAt());
        entity.setItems(purchase.items().stream()
                .map(item -> new PurchaseItemEmbeddable(
                        item.productId(),
                        item.productName(),
                        item.unitPrice(),
                        item.quantity(),
                        item.lineTotal(),
                        item.purchasedAt()))
                .toList());
        return entity;
    }

    private Purchase toDomain(PurchaseEntity entity) {
        return new Purchase(
                entity.getId(),
                entity.getCartId(),
                entity.getUserId(),
                entity.getCartType(),
                entity.getTotalAmount(),
                entity.getCheckedOutAt(),
                entity.getItems().stream()
                        .map(item -> new PurchaseItem(
                                item.getProductId(),
                                item.getProductName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.getLineTotal(),
                                item.getPurchasedAt()))
                        .toList());
    }
}