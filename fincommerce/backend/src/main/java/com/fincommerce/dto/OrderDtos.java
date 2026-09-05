package com.fincommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    public static class OrderItemDto {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;

        public OrderItemDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public static class OrderDto {
        private Long id;
        private String orderNumber;
        private List<OrderItemDto> items;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal deliveryFee;
        private BigDecimal taxAmount;
        private BigDecimal finalTotal;
        private String addressJson;
        private String paymentMethod;
        private String paymentStatus;
        private String orderStatus;
        private String trackingNumber;
        private String estimatedDelivery;
        private LocalDateTime createdAt;

        public OrderDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public List<OrderItemDto> getItems() { return items; }
        public void setItems(List<OrderItemDto> items) { this.items = items; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

        public BigDecimal getDeliveryFee() { return deliveryFee; }
        public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

        public BigDecimal getFinalTotal() { return finalTotal; }
        public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }

        public String getAddressJson() { return addressJson; }
        public void setAddressJson(String addressJson) { this.addressJson = addressJson; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

        public String getEstimatedDelivery() { return estimatedDelivery; }
        public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class ReturnRequestDto {
        private Long orderId;
        private Long orderItemId;
        private String reason;
        private String imageUrl;

        public ReturnRequestDto() {}

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public Long getOrderItemId() { return orderItemId; }
        public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
