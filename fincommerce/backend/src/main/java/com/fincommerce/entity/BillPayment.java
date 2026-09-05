package com.fincommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_payments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "roles", "password", "transactionPin"})
    private User user;

    private String category; // ELECTRICITY, WATER, MOBILE_RECHARGE, INTERNET, GAS, DTH, INSURANCE, CREDIT_CARD
    private String providerName;
    private String customerNumber;
    private BigDecimal amount;
    private String transactionRef;
    private String status; // SUCCESS, PENDING, FAILED

    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        this.paidAt = LocalDateTime.now();
    }

    public BillPayment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
