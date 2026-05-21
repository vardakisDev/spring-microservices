package com.interview.cartservice.application.usecase.command;

import java.util.UUID;

public record RemoveCartItemCommand(UUID cartId, UUID productId, int quantity) {
}