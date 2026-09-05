package com.fincommerce.repository;

import com.fincommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByCategorySlug(String slug);
    List<Product> findByIsFeaturedTrue();

    @Query("SELECT p FROM Product p WHERE " +
           "(:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchProducts(@Param("query") String query, @Param("categoryId") Long categoryId);
}
