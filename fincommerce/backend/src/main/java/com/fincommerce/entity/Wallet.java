package com.fincommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    private BigDecimal totalSpent = BigDecimal.ZERO;
    private BigDecimal monthlySpent = BigDecimal.ZERO;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Wallet() {}

    public Wallet(User user, BigDecimal balance) {
        this.user = user;
        this.balance = balance;
        this.availableBalance = balance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

    public BigDecimal getMonthlySpent() { return monthlySpent; }
    public void setMonthlySpent(BigDecimal monthlySpent) { this.monthlySpent = monthlySpent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
