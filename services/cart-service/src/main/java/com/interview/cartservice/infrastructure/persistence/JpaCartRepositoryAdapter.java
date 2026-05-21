package com.interview.cartservice.infrastructure.persistence;

import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.domain.model.Cart;
import com.interview.cartservice.domain.model.CartItem;
import com.interview.cartservice.domain.service.PricingCalculator;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCartRepositoryAdapter implements CartRepositoryPort {

    private final CartJpaRepository cartJpaRepository;
    private final PricingCalculator pricingCalculator;

    public JpaCartRepositoryAdapter(CartJpaRepository cartJpaRepository, PricingCalculator pricingCalculator) {
        this.cartJpaRepository = cartJpaRepository;
        this.pricingCalculator = pricingCalculator;
    }

    @Override
    public Optional<Cart> findById(UUID cartId) {
        return cartJpaRepository.findById(cartId).map(this::toDomain);
    }

    @Override
    public Cart save(Cart cart) {
        return toDomain(cartJpaRepository.save(toEntity(cart)));
    }

    @Override
    public void deleteById(UUID cartId) {
        cartJpaRepository.deleteById(cartId);
    }

    private CartEntity toEntity(Cart cart) {
        CartEntity entity = new CartEntity();
        entity.setId(cart.id());
        entity.setUserId(cart.userId());
        entity.setCartType(cart.cartType());
        entity.setStatus(cart.status());
        entity.setCreatedAt(cart.createdAt());
        entity.setUpdatedAt(cart.updatedAt());
        entity.setItems(cart.items().stream()
                .map(item -> new CartItemEmbeddable(item.productId(), item.productName(), item.unitPrice(), item.quantity()))
                .toList());
        return entity;
    }

    private Cart toDomain(CartEntity entity) {
        return Cart.rehydrate(
                entity.getId(),
                entity.getUserId(),
                entity.getCartType(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getItems().stream()
                        .map(item -> new CartItem(item.getProductId(), item.getProductName(), item.getUnitPrice(), item.getQuantity()))
                        .toList(),
                pricingCalculator);
    }
}