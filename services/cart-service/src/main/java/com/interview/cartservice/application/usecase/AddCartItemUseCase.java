package com.interview.cartservice.application.usecase;

import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.cartservice.application.port.out.CartRepositoryPort;
import com.interview.cartservice.application.port.out.ProductCatalogPort;
import com.interview.cartservice.application.usecase.command.AddCartItemCommand;
import com.interview.cartservice.domain.model.Cart;

import java.util.Objects;

public class AddCartItemUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final ProductCatalogPort productCatalogPort;

    public AddCartItemUseCase(CartRepositoryPort cartRepositoryPort, ProductCatalogPort productCatalogPort) {
        this.cartRepositoryPort = Objects.requireNonNull(cartRepositoryPort, "cartRepositoryPort must not be null");
        this.productCatalogPort = Objects.requireNonNull(productCatalogPort, "productCatalogPort must not be null");
    }

    public Cart execute(AddCartItemCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Cart cart = cartRepositoryPort.findById(command.cartId())
                .orElseThrow(() -> new ResourceNotFoundException("cart not found: " + command.cartId()));

        ProductCatalogPort.ProductSnapshot product = productCatalogPort.getProduct(command.productId());
        cart.addItem(product.id(), product.name(), product.unitPrice(), command.quantity());
        return cartRepositoryPort.save(cart);
    }
}