package com.shoppingcart.backend.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        String sku,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal lineTotal
) {
}