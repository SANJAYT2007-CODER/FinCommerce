package com.fincommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CheckoutDtos {

    public static class CheckoutRequest {
        @NotNull(message = "Shipping Address ID is required")
        private Long addressId;

        @NotBlank(message = "Payment Method is required")
        private String paymentMethod; // WALLET, UPI, CARD, NET_BANKING, CASH_ON_DELIVERY

        private String couponCode;
        private String pin; // Required if paying via Wallet
        private String cardDetails;

        public CheckoutRequest() {}

        public Long getAddressId() { return addressId; }
        public void setAddressId(Long addressId) { this.addressId = addressId; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getCouponCode() { return couponCode; }
        public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }

        public String getCardDetails() { return cardDetails; }
        public void setCardDetails(String cardDetails) { this.cardDetails = cardDetails; }
    }

    public static class OrderConfirmationResponse {
        private String orderNumber;
        private BigDecimal finalTotal;
        private String paymentStatus;
        private String orderStatus;
        private String trackingNumber;
        private String estimatedDelivery;
        private String message;

        public OrderConfirmationResponse() {}

        public OrderConfirmationResponse(String orderNumber, BigDecimal finalTotal, String paymentStatus, String orderStatus, String trackingNumber, String estimatedDelivery, String message) {
            this.orderNumber = orderNumber;
            this.finalTotal = finalTotal;
            this.paymentStatus = paymentStatus;
            this.orderStatus = orderStatus;
            this.trackingNumber = trackingNumber;
            this.estimatedDelivery = estimatedDelivery;
            this.message = message;
        }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public BigDecimal getFinalTotal() { return finalTotal; }
        public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

        public String getEstimatedDelivery() { return estimatedDelivery; }
        public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
