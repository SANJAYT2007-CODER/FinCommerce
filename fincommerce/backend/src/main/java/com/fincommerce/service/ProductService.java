package com.fincommerce.service;

import com.fincommerce.dto.ProductDtos;
import com.fincommerce.entity.Category;
import com.fincommerce.entity.Product;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.CategoryRepository;
import com.fincommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ProductDtos.CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    public List<ProductDtos.ProductDto> searchProducts(String query, Long categoryId) {
        List<Product> products = productRepository.searchProducts(query, categoryId);
        return products.stream().map(this::mapToProductDto).collect(Collectors.toList());
    }

    public ProductDtos.ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductDto(product);
    }

    public List<ProductDtos.ProductDto> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    private ProductDtos.CategoryDto mapToCategoryDto(Category category) {
        ProductDtos.CategoryDto dto = new ProductDtos.CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        return dto;
    }

    public ProductDtos.ProductDto mapToProductDto(Product product) {
        ProductDtos.ProductDto dto = new ProductDtos.ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setPrice(product.getPrice());
        dto.setDiscountPercent(product.getDiscountPercent());

        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - (product.getDiscountPercent() != null ? product.getDiscountPercent() : 0))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        dto.setDiscountedPrice(product.getPrice().multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP));

        dto.setStockQuantity(product.getStockQuantity());
        dto.setRating(product.getRating());
        dto.setReviewsCount(product.getReviewsCount());
        dto.setDescription(product.getDescription());
        dto.setSpecificationsJson(product.getSpecificationsJson());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsFeatured(product.getIsFeatured());
        return dto;
    }
}
