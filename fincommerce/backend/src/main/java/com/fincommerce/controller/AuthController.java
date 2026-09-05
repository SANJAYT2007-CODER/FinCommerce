package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.AuthDtos;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        AuthDtos.AuthResponse response = authService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        AuthDtos.AuthResponse response = authService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AuthDtos.ChangePasswordRequest request) {
        authService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PostMapping("/set-pin")
    public ResponseEntity<ApiResponse<String>> setTransactionPin(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AuthDtos.SetPinRequest request) {
        authService.setTransactionPin(userPrincipal.getId(), request.getPin());
        return ResponseEntity.ok(ApiResponse.success("Transaction PIN updated successfully"));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<AuthDtos.OtpResponse>> sendOtp(@Valid @RequestBody AuthDtos.SendOtpRequest request) {
        AuthDtos.OtpResponse response = authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> verifyOtp(@Valid @RequestBody AuthDtos.VerifyOtpRequest request) {
        AuthDtos.AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Mobile number verified successfully", response));
    }
}

