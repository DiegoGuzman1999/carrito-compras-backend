package com.shoppingcart.backend.service;

import com.shoppingcart.backend.dto.cart.AddCartItemRequest;
import com.shoppingcart.backend.dto.cart.CartResponse;
import com.shoppingcart.backend.dto.cart.CreateCartRequest;

public interface CartService {

    CartResponse createCart(CreateCartRequest request);

    CartResponse addProductToCart(Long cartId, AddCartItemRequest request);

    CartResponse removeProductFromCart(Long cartId, Long productId);

    CartResponse getCartById(Long cartId);
}