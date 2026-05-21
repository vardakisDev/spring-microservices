package com.interview.productservice.application.usecase.command;

import java.math.BigDecimal;

public record UpdateProductCommand(String name, String description, BigDecimal price) {
}