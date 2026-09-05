package com.fincommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private String dateOfBirth;
    private String gender;
    private String profilePhoto;
    
    @JsonIgnore
    private String transactionPin;
    private Boolean biometricEnabled = false;

    // Notification & Alert Preferences
    private String dailySummaryTime = "08:00 PM";
    private Boolean dailySummaryEnabled = true;
    private String savingsReminderTime = "07:00 PM";
    private Boolean savingsReminderEnabled = true;
    private Integer budgetAlertPercentage = 80;
    private String notificationPreferences = "PAYMENTS,TRANSACTIONS,SAVINGS,BUDGET,ORDERS,FRAUD,LOGIN,OFFERS";


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

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

    public User() {}

    public User(String fullName, String email, String mobileNumber, String password) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getTransactionPin() { return transactionPin; }
    public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }

    public Boolean getBiometricEnabled() { return biometricEnabled; }
    public void setBiometricEnabled(Boolean biometricEnabled) { this.biometricEnabled = biometricEnabled; }

    public String getDailySummaryTime() { return dailySummaryTime; }
    public void setDailySummaryTime(String dailySummaryTime) { this.dailySummaryTime = dailySummaryTime; }

    public Boolean getDailySummaryEnabled() { return dailySummaryEnabled; }
    public void setDailySummaryEnabled(Boolean dailySummaryEnabled) { this.dailySummaryEnabled = dailySummaryEnabled; }

    public String getSavingsReminderTime() { return savingsReminderTime; }
    public void setSavingsReminderTime(String savingsReminderTime) { this.savingsReminderTime = savingsReminderTime; }

    public Boolean getSavingsReminderEnabled() { return savingsReminderEnabled; }
    public void setSavingsReminderEnabled(Boolean savingsReminderEnabled) { this.savingsReminderEnabled = savingsReminderEnabled; }

    public Integer getBudgetAlertPercentage() { return budgetAlertPercentage; }
    public void setBudgetAlertPercentage(Integer budgetAlertPercentage) { this.budgetAlertPercentage = budgetAlertPercentage; }

    public String getNotificationPreferences() { return notificationPreferences; }
    public void setNotificationPreferences(String notificationPreferences) { this.notificationPreferences = notificationPreferences; }

    public Set<Role> getRoles() { return roles; }

    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
