package com.fincommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsDtos {

    public static class CreateGoalRequest {
        @NotBlank(message = "Goal Name is required")
        private String goalName;

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "1.0", message = "Target amount must be greater than 0")
        private BigDecimal targetAmount;

        private BigDecimal initialSavingAmount = BigDecimal.ZERO;

        private LocalDate targetDate;

        @NotBlank(message = "Goal category is required")
        private String category; // Emergency Fund, Education, Travel, New Phone, Laptop, Vehicle, Home, Wedding, Investment, Other

        private String description;

        private String pin; // Required if initialSavingAmount > 0

        public CreateGoalRequest() {}

        public String getGoalName() { return goalName; }
        public void setGoalName(String goalName) { this.goalName = goalName; }

        public BigDecimal getTargetAmount() { return targetAmount; }
        public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

        public BigDecimal getInitialSavingAmount() { return initialSavingAmount; }
        public void setInitialSavingAmount(BigDecimal initialSavingAmount) { this.initialSavingAmount = initialSavingAmount; }

        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class UpdateGoalRequest {
        private String goalName;
        private BigDecimal targetAmount;
        private LocalDate targetDate;
        private String category;
        private String description;

        public UpdateGoalRequest() {}

        public String getGoalName() { return goalName; }
        public void setGoalName(String goalName) { this.goalName = goalName; }

        public BigDecimal getTargetAmount() { return targetAmount; }
        public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class AddMoneyRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.0", message = "Amount must be greater than 0")
        private BigDecimal amount;

        @NotBlank(message = "Transaction PIN is required")
        private String pin;

        public AddMoneyRequest() {}

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class WithdrawRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.0", message = "Amount must be greater than 0")
        private BigDecimal amount;

        @NotBlank(message = "Transaction PIN is required")
        private String pin;

        public WithdrawRequest() {}

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class SavingsSummaryDto {
        private BigDecimal totalSavedAmount;
        private long totalSavingsGoals;
        private long activeSavingsGoals;
        private long completedGoals;
        private BigDecimal availableWalletBalance;
        private BigDecimal monthlySavings;
        private double savingsProgress;

        public SavingsSummaryDto() {}

        public BigDecimal getTotalSavedAmount() { return totalSavedAmount; }
        public void setTotalSavedAmount(BigDecimal totalSavedAmount) { this.totalSavedAmount = totalSavedAmount; }

        public long getTotalSavingsGoals() { return totalSavingsGoals; }
        public void setTotalSavingsGoals(long totalSavingsGoals) { this.totalSavingsGoals = totalSavingsGoals; }

        public long getActiveSavingsGoals() { return activeSavingsGoals; }
        public void setActiveSavingsGoals(long activeSavingsGoals) { this.activeSavingsGoals = activeSavingsGoals; }

        public long getCompletedGoals() { return completedGoals; }
        public void setCompletedGoals(long completedGoals) { this.completedGoals = completedGoals; }

        public BigDecimal getAvailableWalletBalance() { return availableWalletBalance; }
        public void setAvailableWalletBalance(BigDecimal availableWalletBalance) { this.availableWalletBalance = availableWalletBalance; }

        public BigDecimal getMonthlySavings() { return monthlySavings; }
        public void setMonthlySavings(BigDecimal monthlySavings) { this.monthlySavings = monthlySavings; }

        public double getSavingsProgress() { return savingsProgress; }
        public void setSavingsProgress(double savingsProgress) { this.savingsProgress = savingsProgress; }
    }

    public static class GoalResponse {
        private Long id;
        private String goalName;
        private String category;
        private BigDecimal targetAmount;
        private BigDecimal savedAmount;
        private BigDecimal remainingAmount;
        private double progressPercentage;
        private LocalDate targetDate;
        private String description;
        private String status;

        public GoalResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getGoalName() { return goalName; }
        public void setGoalName(String goalName) { this.goalName = goalName; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getTargetAmount() { return targetAmount; }
        public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

        public BigDecimal getSavedAmount() { return savedAmount; }
        public void setSavedAmount(BigDecimal savedAmount) { this.savedAmount = savedAmount; }

        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

        public double getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }

        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
