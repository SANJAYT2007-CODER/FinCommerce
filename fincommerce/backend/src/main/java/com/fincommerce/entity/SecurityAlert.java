package com.fincommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_alerts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SecurityAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "roles", "password", "transactionPin"})
    private User user;

    private String transactionId;
    
    @Column(length = 1000)
    private String alertMessage;
    
    private String severity; // HIGH, MEDIUM, LOW
    private Boolean isReviewed = false;
    private String status; // DETECTED, REVIEWED, RESOLVED, DISMISSED

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public SecurityAlert() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Boolean getIsReviewed() { return isReviewed; }
    public void setIsReviewed(Boolean isReviewed) { this.isReviewed = isReviewed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
