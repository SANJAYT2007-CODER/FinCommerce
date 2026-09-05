package com.fincommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Full Name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^[0-9]{10,12}$", message = "Mobile number must be 10-12 digits")
        private String mobileNumber;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Confirm Password is required")
        private String confirmPassword;

        public RegisterRequest() {}

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class AuthResponse {
        private String accessToken;
        private String tokenType = "Bearer";
        private Long userId;
        private String fullName;
        private String email;
        private String mobileNumber;
        private String role;

        public AuthResponse() {}

        public AuthResponse(String accessToken, Long userId, String fullName, String email, String mobileNumber, String role) {
            this.accessToken = accessToken;
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.mobileNumber = mobileNumber;
            this.role = role;
        }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String newPassword;

        public ChangePasswordRequest() {}

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class SetPinRequest {
        @NotBlank(message = "Transaction PIN is required")
        @Size(min = 4, max = 6, message = "PIN must be 4 to 6 digits")
        private String pin;

        public SetPinRequest() {}

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }

    public static class SendOtpRequest {
        @NotBlank(message = "Mobile number or email is required")
        private String identifier; // mobile number or email

        public SendOtpRequest() {}

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
    }

    public static class VerifyOtpRequest {
        @NotBlank(message = "Mobile number or email is required")
        private String identifier;

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        private String otp;

        private String deviceInfo;

        public VerifyOtpRequest() {}

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }

        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }

        public String getDeviceInfo() { return deviceInfo; }
        public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    }

    public static class OtpResponse {
        private String message;
        private String mobileNumber;
        private String devOtp;

        public OtpResponse() {}

        public OtpResponse(String message, String mobileNumber, String devOtp) {
            this.message = message;
            this.mobileNumber = mobileNumber;
            this.devOtp = devOtp;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

        public String getDevOtp() { return devOtp; }
        public void setDevOtp(String devOtp) { this.devOtp = devOtp; }
    }
}

