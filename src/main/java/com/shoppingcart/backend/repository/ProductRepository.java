package com.shoppingcart.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}