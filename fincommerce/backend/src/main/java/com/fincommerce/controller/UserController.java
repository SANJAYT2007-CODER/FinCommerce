package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.entity.Address;
import com.fincommerce.entity.User;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.getUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody User updatedDetails) {
        User user = userService.updateUserProfile(userPrincipal.getId(), updatedDetails);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
    }

    @PostMapping("/biometric")
    public ResponseEntity<ApiResponse<User>> toggleBiometric(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("enabled", false);
        User user = userService.toggleBiometric(userPrincipal.getId(), enabled);
        return ResponseEntity.ok(ApiResponse.success("Biometric security updated", user));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<Address>>> getAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Address> addresses = userService.getUserAddresses(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved", addresses));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<Address>> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Address address) {
        Address saved = userService.addAddress(userPrincipal.getId(), address);
        return ResponseEntity.ok(ApiResponse.success("Address added successfully", saved));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        userService.deleteAddress(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<User>> updatePreferences(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody User updatedPrefs) {
        User user = userService.updatePreferences(userPrincipal.getId(), updatedPrefs);
        return ResponseEntity.ok(ApiResponse.success("Notification preferences updated successfully", user));
    }
}

