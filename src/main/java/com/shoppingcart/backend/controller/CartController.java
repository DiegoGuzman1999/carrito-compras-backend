package com.shoppingcart.backend.controller;

import com.shoppingcart.backend.dto.cart.AddCartItemRequest;
import com.shoppingcart.backend.dto.cart.CartResponse;
import com.shoppingcart.backend.dto.cart.CreateCartRequest;
import com.shoppingcart.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@Valid @RequestBody CreateCartRequest request) {
        CartResponse response = cartService.createCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addProductToCart(
            @PathVariable Long cartId,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.addProductToCart(cartId, request));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponse> removeProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(cartService.removeProductFromCart(cartId, productId));
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCartById(cartId));
    }
}