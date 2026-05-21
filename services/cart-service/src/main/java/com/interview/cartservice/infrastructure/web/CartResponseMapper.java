package com.interview.cartservice.infrastructure.web;

import com.interview.cartservice.api.model.CartItemResponse;
import com.interview.cartservice.api.model.CartResponse;
import com.interview.cartservice.api.model.CheckoutItemResponse;
import com.interview.cartservice.api.model.CheckoutResponse;
import com.interview.cartservice.api.model.PurchasedProductSummary;
import com.interview.cartservice.api.model.TopPurchasedProductsResponse;
import com.interview.cartservice.application.usecase.query.PurchasedProductView;
import com.interview.cartservice.domain.model.Cart;
import com.interview.cartservice.domain.model.CartItem;
import com.interview.cartservice.domain.model.PricingSummary;
import com.interview.cartservice.domain.model.Purchase;
import com.interview.cartservice.domain.model.PurchaseItem;

import java.util.List;
import java.util.UUID;

final class CartResponseMapper {

    CartResponse toCartResponse(Cart cart) {
        return new CartResponse()
                .id(cart.id())
                .userId(cart.userId())
                .cartType(com.interview.cartservice.api.model.CartType.valueOf(cart.cartType().name()))
                .status(com.interview.cartservice.api.model.CartStatus.valueOf(cart.status().name()))
                .items(cart.items().stream().map(this::toCartItemResponse).toList())
                .pricing(toPricingSummary(cart.pricingSummary()))
                .createdAt(cart.createdAt())
                .updatedAt(cart.updatedAt());
    }

    CheckoutResponse toCheckoutResponse(Purchase purchase) {
        return new CheckoutResponse()
                .purchaseId(purchase.id())
                .cartId(purchase.cartId())
                .userId(purchase.userId())
                .cartType(com.interview.cartservice.api.model.CartType.valueOf(purchase.cartType().name()))
                .totalAmount(purchase.totalAmount())
                .checkedOutAt(purchase.checkedOutAt())
                .items(purchase.items().stream().map(this::toCheckoutItemResponse).toList());
    }

    TopPurchasedProductsResponse toTopPurchasedProductsResponse(UUID userId, List<PurchasedProductView> products) {
        return new TopPurchasedProductsResponse()
                .userId(userId)
                .products(products.stream().map(this::toPurchasedProductSummary).toList());
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        return new CartItemResponse()
                .productId(item.productId())
                .productName(item.productName())
                .unitPrice(item.unitPrice())
                .quantity(item.quantity())
                .lineTotal(item.lineTotal());
    }

    private com.interview.cartservice.api.model.PricingSummary toPricingSummary(PricingSummary pricingSummary) {
        return new com.interview.cartservice.api.model.PricingSummary()
                .subtotal(pricingSummary.subtotal())
                .discountAmount(pricingSummary.discountAmount())
                .totalAmount(pricingSummary.totalAmount())
                .totalQuantity(pricingSummary.totalQuantity());
    }

    private CheckoutItemResponse toCheckoutItemResponse(PurchaseItem item) {
        return new CheckoutItemResponse()
                .productId(item.productId())
                .productName(item.productName())
                .unitPrice(item.unitPrice())
                .quantity(item.quantity())
                .lineTotal(item.lineTotal());
    }

    private PurchasedProductSummary toPurchasedProductSummary(PurchasedProductView product) {
        return new PurchasedProductSummary()
                .productId(product.productId())
                .productName(product.productName())
                .unitPrice(product.unitPrice())
                .purchaseCount(Math.toIntExact(product.purchaseCount()));
    }
}