package com.fincommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductDtos {

    public static class CategoryDto {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private String imageUrl;

        public CategoryDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class ProductDto {
        private Long id;
        private String name;
        private String brand;
        private Long categoryId;
        private String categoryName;
        private BigDecimal price;
        private Integer discountPercent;
        private BigDecimal discountedPrice;
        private Integer stockQuantity;
        private Double rating;
        private Integer reviewsCount;
        private String description;
        private String specificationsJson;
        private String imageUrl;
        private Boolean isFeatured;

        public ProductDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getDiscountPercent() { return discountPercent; }
        public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

        public BigDecimal getDiscountedPrice() { return discountedPrice; }
        public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }

        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }

        public Integer getReviewsCount() { return reviewsCount; }
        public void setReviewsCount(Integer reviewsCount) { this.reviewsCount = reviewsCount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getSpecificationsJson() { return specificationsJson; }
        public void setSpecificationsJson(String specificationsJson) { this.specificationsJson = specificationsJson; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public Boolean getIsFeatured() { return isFeatured; }
        public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    }
}
