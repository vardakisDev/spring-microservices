package com.interview.productservice.application.usecase.command;

import java.math.BigDecimal;

public record CreateProductCommand(String name, String description, BigDecimal price) {
}