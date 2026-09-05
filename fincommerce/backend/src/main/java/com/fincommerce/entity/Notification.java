package com.fincommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "roles", "password", "transactionPin"})
    private User user;

    private String title;
    
    @Column(length = 1000)
    private String message;
    
    private String type; // PAYMENT_SUCCESS, PAYMENT_FAILED, MONEY_RECEIVED, MONEY_SENT, ORDER_PLACED, ORDER_SHIPPED, ORDER_DELIVERED, REFUND_PROCESSED, SUSPICIOUS_ALERT, BUDGET_EXCEEDED, COUPON_AVAILABLE
    private Boolean isRead = false;
    private String linkUrl;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
