package com.interview.cartservice.application.usecase.command;

import java.util.UUID;

public record AddCartItemCommand(UUID cartId, UUID productId, int quantity) {
}