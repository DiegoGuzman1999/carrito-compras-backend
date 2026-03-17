package com.shoppingcart.backend.service.impl;

import com.shoppingcart.backend.dto.cart.AddCartItemRequest;
import com.shoppingcart.backend.dto.cart.CartItemResponse;
import com.shoppingcart.backend.dto.cart.CartResponse;
import com.shoppingcart.backend.dto.cart.CreateCartRequest;
import com.shoppingcart.backend.entity.*;
import com.shoppingcart.backend.exception.BusinessException;
import com.shoppingcart.backend.exception.ResourceNotFoundException;
import com.shoppingcart.backend.repository.CartItemRepository;
import com.shoppingcart.backend.repository.CartRepository;
import com.shoppingcart.backend.repository.ProductRepository;
import com.shoppingcart.backend.repository.UserRepository;
import com.shoppingcart.backend.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CartResponse createCart(CreateCartRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + request.userId() + " was not found"));

        if (cartRepository.existsByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)) {
            throw new BusinessException("User with id " + user.getId() + " already has an active cart");
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setStatus(CartStatus.ACTIVE);

        Cart savedCart = cartRepository.save(cart);

        return getCartById(savedCart.getId());
    }

    @Override
    @Transactional
    public CartResponse addProductToCart(Long cartId, AddCartItemRequest request) {
        Cart cart = getActiveCartOrThrow(cartId);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + request.productId() + " was not found"));

        if (Boolean.FALSE.equals(product.getActive())) {
            throw new BusinessException("Product with id " + product.getId() + " is inactive");
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, product.getId()).orElse(null);

        int newQuantity = request.quantity();

        if (cartItem != null) {
            newQuantity = cartItem.getQuantity() + request.quantity();
        }

        validateStock(product, newQuantity);

        if (cartItem == null) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());
            cart.getItems().add(newItem);
        } else {
            cartItem.setQuantity(newQuantity);
        }

        cartRepository.save(cart);

        return getCartById(cartId);
    }

    @Override
    @Transactional
    public CartResponse removeProductFromCart(Long cartId, Long productId) {
        Cart cart = getDetailedActiveCartOrThrow(cartId);

        CartItem itemToRemove = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with id " + productId + " is not present in cart " + cartId
                ));

        cart.getItems().remove(itemToRemove);
        cartRepository.save(cart);

        return getCartById(cartId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartById(Long cartId) {
        Cart cart = getDetailedActiveCartOrThrow(cartId);
        return mapToResponse(cart);
    }

    private Cart getActiveCartOrThrow(Long cartId) {
        return cartRepository.findByIdAndStatus(cartId, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart with id " + cartId + " was not found"));
    }

    private Cart getDetailedActiveCartOrThrow(Long cartId) {
        return cartRepository.findDetailedByIdAndStatus(cartId, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart with id " + cartId + " was not found"));
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new BusinessException(
                    "Insufficient stock for product " + product.getSku() +
                    ". Available: " + product.getStock() +
                    ", requested: " + requestedQuantity
            );
        }
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getProduct().getPrice();
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartItemResponse(
                            item.getProduct().getId(),
                            item.getProduct().getSku(),
                            item.getProduct().getName(),
                            unitPrice,
                            item.getQuantity(),
                            lineTotal
                    );
                })
                .toList();

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String userFullName = cart.getUser().getFirstName() + " " + cart.getUser().getLastName();

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                userFullName,
                cart.getStatus().name(),
                items,
                totalItems,
                totalAmount,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }
}