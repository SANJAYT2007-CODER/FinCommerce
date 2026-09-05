package com.fincommerce.repository;

import com.fincommerce.entity.Product;
import com.fincommerce.entity.Wishlist;
import com.fincommerce.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishlistAndProduct(Wishlist wishlist, Product product);
    void deleteByWishlistAndProduct(Wishlist wishlist, Product product);
}
