package com.fincommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FinancialDtos {

    public static class AddExpenseRequest {
        @NotBlank(message = "Category is required")
        private String category; // FOOD, SHOPPING, TRAVEL, BILLS, ENTERTAINMENT, EDUCATION, HEALTHCARE, OTHER

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;

        private LocalDate expenseDate;
        private String description;

        public AddExpenseRequest() {}

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDate getExpenseDate() { return expenseDate; }
        public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class SetBudgetRequest {
        @NotBlank(message = "Category is required")
        private String category; // ALL, FOOD, SHOPPING, etc.

        @NotNull(message = "Monthly limit is required")
        @DecimalMin(value = "1.00", message = "Limit must be at least 1.00")
        private BigDecimal monthlyLimit;

        @NotBlank(message = "Month-Year is required (e.g., 2026-09)")
        private String monthYear;

        public SetBudgetRequest() {}

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getMonthlyLimit() { return monthlyLimit; }
        public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

        public String getMonthYear() { return monthYear; }
        public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    }

    public static class BillPaymentRequest {
        @NotBlank(message = "Category is required")
        private String category; // ELECTRICITY, WATER, MOBILE_RECHARGE, INTERNET, GAS, DTH, INSURANCE, CREDIT_CARD

        @NotBlank(message = "Provider name is required")
        private String providerName;

        @NotBlank(message = "Customer account/mobile number is required")
        private String customerNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        private BigDecimal amount;

        @NotBlank(message = "PIN is required")
        private String pin;

        public BillPaymentRequest() {}

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getProviderName() { return providerName; }
        public void setProviderName(String providerName) { this.providerName = providerName; }

        public String getCustomerNumber() { return customerNumber; }
        public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class AddBankAccountRequest {
        @NotBlank(message = "Bank name is required")
        private String bankName;

        @NotBlank(message = "Account holder name is required")
        private String accountHolderName;

        @NotBlank(message = "Account number is required")
        private String accountNumber;

        @NotBlank(message = "IFSC code is required")
        private String ifscCode;

        private Boolean isPrimary = false;

        public AddBankAccountRequest() {}

        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }

        public String getAccountHolderName() { return accountHolderName; }
        public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

        public String getIfscCode() { return ifscCode; }
        public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

        public Boolean getIsPrimary() { return isPrimary; }
        public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    }
}
