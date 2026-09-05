package com.fincommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WalletDtos {

    public static class WalletDto {
        private Long walletId;
        private BigDecimal balance;
        private BigDecimal availableBalance;
        private BigDecimal totalSpent;
        private BigDecimal monthlySpent;
        private String userFullName;
        private String userMobile;

        public WalletDto() {}

        public Long getWalletId() { return walletId; }
        public void setWalletId(Long walletId) { this.walletId = walletId; }

        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }

        public BigDecimal getAvailableBalance() { return availableBalance; }
        public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

        public BigDecimal getTotalSpent() { return totalSpent; }
        public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

        public BigDecimal getMonthlySpent() { return monthlySpent; }
        public void setMonthlySpent(BigDecimal monthlySpent) { this.monthlySpent = monthlySpent; }

        public String getUserFullName() { return userFullName; }
        public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

        public String getUserMobile() { return userMobile; }
        public void setUserMobile(String userMobile) { this.userMobile = userMobile; }
    }

    public static class AddMoneyRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        private BigDecimal amount;

        @NotBlank(message = "Payment method is required")
        private String paymentMethod; // UPI, DEBIT_CARD, CREDIT_CARD, NET_BANKING

        private String cardOrUpiDetails;

        public AddMoneyRequest() {}

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getCardOrUpiDetails() { return cardOrUpiDetails; }
        public void setCardOrUpiDetails(String cardOrUpiDetails) { this.cardOrUpiDetails = cardOrUpiDetails; }
    }

    public static class WithdrawRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        private BigDecimal amount;

        @NotNull(message = "Bank Account ID is required")
        private Long bankAccountId;

        @NotBlank(message = "Transaction PIN is required")
        private String pin;

        public WithdrawRequest() {}

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public Long getBankAccountId() { return bankAccountId; }
        public void setBankAccountId(Long bankAccountId) { this.bankAccountId = bankAccountId; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class SendMoneyRequest {
        @NotBlank(message = "Receiver identifier (mobile, email, or UPI ID) is required")
        private String receiverIdentifier;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        private BigDecimal amount;

        private String note;

        @NotBlank(message = "Security PIN is required")
        private String pin;

        public SendMoneyRequest() {}

        public String getReceiverIdentifier() { return receiverIdentifier; }
        public void setReceiverIdentifier(String receiverIdentifier) { this.receiverIdentifier = receiverIdentifier; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class UpiPaymentRequest {
        @NotBlank(message = "UPI ID is required")
        private String upiId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        private BigDecimal amount;

        private String note;
        private String pin;

        public UpiPaymentRequest() {}

        public String getUpiId() { return upiId; }
        public void setUpiId(String upiId) { this.upiId = upiId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class QRGenerateResponse {
        private String upiId;
        private String qrCodeDataUrl;
        private String shareableLink;

        public QRGenerateResponse() {}

        public QRGenerateResponse(String upiId, String qrCodeDataUrl, String shareableLink) {
            this.upiId = upiId;
            this.qrCodeDataUrl = qrCodeDataUrl;
            this.shareableLink = shareableLink;
        }

        public String getUpiId() { return upiId; }
        public void setUpiId(String upiId) { this.upiId = upiId; }

        public String getQrCodeDataUrl() { return qrCodeDataUrl; }
        public void setQrCodeDataUrl(String qrCodeDataUrl) { this.qrCodeDataUrl = qrCodeDataUrl; }

        public String getShareableLink() { return shareableLink; }
        public void setShareableLink(String shareableLink) { this.shareableLink = shareableLink; }
    }
}
