package com.fincommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CartDtos {

    public static class AddToCartRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity = 1;

        public AddToCartRequest() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class UpdateCartItemRequest {
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        public UpdateCartItemRequest() {}

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class CartItemDto {
        private Long id;
        private Long productId;
        private String productName;
        private String productBrand;
        private String productImage;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;

        public CartItemDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getProductBrand() { return productBrand; }
        public void setProductBrand(String productBrand) { this.productBrand = productBrand; }

        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public static class CartDto {
        private Long cartId;
        private List<CartItemDto> items;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private String appliedCouponCode;
        private BigDecimal deliveryFee;
        private BigDecimal taxAmount;
        private BigDecimal finalTotal;

        public CartDto() {}

        public Long getCartId() { return cartId; }
        public void setCartId(Long cartId) { this.cartId = cartId; }

        public List<CartItemDto> getItems() { return items; }
        public void setItems(List<CartItemDto> items) { this.items = items; }

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

        public String getAppliedCouponCode() { return appliedCouponCode; }
        public void setAppliedCouponCode(String appliedCouponCode) { this.appliedCouponCode = appliedCouponCode; }

        public BigDecimal getDeliveryFee() { return deliveryFee; }
        public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

        public BigDecimal getFinalTotal() { return finalTotal; }
        public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }
    }
}
