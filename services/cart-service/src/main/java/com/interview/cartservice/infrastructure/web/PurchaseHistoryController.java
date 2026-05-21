package com.interview.cartservice.infrastructure.web;

import com.interview.cartservice.api.PurchaseHistoryApi;
import com.interview.cartservice.api.model.TopPurchasedProductsResponse;
import com.interview.cartservice.application.usecase.GetTopPurchasedProductsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PurchaseHistoryController implements PurchaseHistoryApi {

    private final GetTopPurchasedProductsUseCase getTopPurchasedProductsUseCase;
    private final CartResponseMapper cartResponseMapper = new CartResponseMapper();

    public PurchaseHistoryController(GetTopPurchasedProductsUseCase getTopPurchasedProductsUseCase) {
        this.getTopPurchasedProductsUseCase = getTopPurchasedProductsUseCase;
    }

    @Override
    public ResponseEntity<TopPurchasedProductsResponse> getTopPurchasedProducts(UUID userId) {
        return ResponseEntity.ok(cartResponseMapper.toTopPurchasedProductsResponse(
                userId,
                getTopPurchasedProductsUseCase.execute(userId)));
    }
}