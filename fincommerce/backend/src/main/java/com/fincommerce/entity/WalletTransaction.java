package com.fincommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "roles", "password", "transactionPin"})
    private User user;

    @Column(nullable = false)
    private String type; // ADD_MONEY, WITHDRAW, SEND, RECEIVE, UPI_PAYMENT, BILL_PAYMENT, SHOPPING, REFUND

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal fee = BigDecimal.ZERO;
    private String senderInfo;
    private String receiverInfo;
    private String paymentMethod; // UPI, DEBIT_CARD, CREDIT_CARD, NET_BANKING, WALLET, CASH_ON_DELIVERY

    @Column(nullable = false)
    private String status; // SUCCESS, PENDING, FAILED, REFUNDED

    private String note;
    private String referenceId; // orderId, billId, etc.
    private Boolean isSuspicious = false;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public WalletTransaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public String getSenderInfo() { return senderInfo; }
    public void setSenderInfo(String senderInfo) { this.senderInfo = senderInfo; }

    public String getReceiverInfo() { return receiverInfo; }
    public void setReceiverInfo(String receiverInfo) { this.receiverInfo = receiverInfo; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public Boolean getIsSuspicious() { return isSuspicious; }
    public void setIsSuspicious(Boolean isSuspicious) { this.isSuspicious = isSuspicious; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
