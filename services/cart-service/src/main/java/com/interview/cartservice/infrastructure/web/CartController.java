package com.interview.cartservice.infrastructure.web;

import com.interview.cartservice.api.CartsApi;
import com.interview.cartservice.api.model.AddCartItemRequest;
import com.interview.cartservice.api.model.CartResponse;
import com.interview.cartservice.api.model.CheckoutResponse;
import com.interview.cartservice.api.model.CreateCartRequest;
import com.interview.cartservice.application.usecase.AddCartItemUseCase;
import com.interview.cartservice.application.usecase.CheckoutCartUseCase;
import com.interview.cartservice.application.usecase.CreateCartUseCase;
import com.interview.cartservice.application.usecase.DeleteCartUseCase;
import com.interview.cartservice.application.usecase.GetCartUseCase;
import com.interview.cartservice.application.usecase.RemoveCartItemUseCase;
import com.interview.cartservice.application.usecase.command.AddCartItemCommand;
import com.interview.cartservice.application.usecase.command.CreateCartCommand;
import com.interview.cartservice.application.usecase.command.RemoveCartItemCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CartController implements CartsApi {

    private final CreateCartUseCase createCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final GetCartUseCase getCartUseCase;
    private final DeleteCartUseCase deleteCartUseCase;
    private final CheckoutCartUseCase checkoutCartUseCase;
    private final CartResponseMapper cartResponseMapper = new CartResponseMapper();

    public CartController(CreateCartUseCase createCartUseCase,
                          AddCartItemUseCase addCartItemUseCase,
                          RemoveCartItemUseCase removeCartItemUseCase,
                          GetCartUseCase getCartUseCase,
                          DeleteCartUseCase deleteCartUseCase,
                          CheckoutCartUseCase checkoutCartUseCase) {
        this.createCartUseCase = createCartUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
        this.removeCartItemUseCase = removeCartItemUseCase;
        this.getCartUseCase = getCartUseCase;
        this.deleteCartUseCase = deleteCartUseCase;
        this.checkoutCartUseCase = checkoutCartUseCase;
    }

    @Override
    public ResponseEntity<CartResponse> addCartItem(UUID cartId, AddCartItemRequest addCartItemRequest) {
        return ResponseEntity.ok(cartResponseMapper.toCartResponse(addCartItemUseCase.execute(
                new AddCartItemCommand(cartId, addCartItemRequest.getProductId(), addCartItemRequest.getQuantity()))));
    }

    @Override
    public ResponseEntity<CheckoutResponse> checkoutCart(UUID cartId) {
        return ResponseEntity.ok(cartResponseMapper.toCheckoutResponse(checkoutCartUseCase.execute(cartId)));
    }

    @Override
    public ResponseEntity<CartResponse> createCart(CreateCartRequest createCartRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponseMapper.toCartResponse(createCartUseCase.execute(
                new CreateCartCommand(
                        createCartRequest.getUserId(),
                        com.interview.cartservice.domain.model.CartType.valueOf(createCartRequest.getCartType().name())))));
    }

    @Override
    public ResponseEntity<Void> deleteCart(UUID cartId) {
        deleteCartUseCase.execute(cartId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CartResponse> getCart(UUID cartId) {
        return ResponseEntity.ok(cartResponseMapper.toCartResponse(getCartUseCase.execute(cartId)));
    }

    @Override
    public ResponseEntity<CartResponse> removeCartItem(UUID cartId, UUID productId, Integer quantity) {
        int requestedQuantity = quantity == null ? 1 : quantity;
        return ResponseEntity.ok(cartResponseMapper.toCartResponse(removeCartItemUseCase.execute(
                new RemoveCartItemCommand(cartId, productId, requestedQuantity))));
    }
}