package com.shoppingcart.backend.dto.cart;

import jakarta.validation.constraints.NotNull;

public record CreateCartRequest(
        @NotNull(message = "userId is required")
        Long userId
) {
}