package com.fincommerce.repository;

import com.fincommerce.entity.Cart;
import com.fincommerce.entity.CartItem;
import com.fincommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    void deleteByCartAndProduct(Cart cart, Product product);
}
