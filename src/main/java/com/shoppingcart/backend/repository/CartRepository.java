package com.shoppingcart.backend.repository;

import com.shoppingcart.backend.entity.Cart;
import com.shoppingcart.backend.entity.CartStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    boolean existsByUserIdAndStatus(Long userId, CartStatus status);

    Optional<Cart> findByIdAndStatus(Long id, CartStatus status);

    @EntityGraph(attributePaths = {"user", "items", "items.product"})
    Optional<Cart> findDetailedByIdAndStatus(Long id, CartStatus status);
}