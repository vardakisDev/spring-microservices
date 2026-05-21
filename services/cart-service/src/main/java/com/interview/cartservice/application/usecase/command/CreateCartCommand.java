package com.interview.cartservice.application.usecase.command;

import com.interview.cartservice.domain.model.CartType;

import java.util.UUID;

public record CreateCartCommand(UUID userId, CartType cartType) {
}