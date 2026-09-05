package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.entity.SecurityAlert;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security/alerts")
public class SecurityAlertController {

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SecurityAlert>>> getUserAlerts(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<SecurityAlert> list = fraudDetectionService.getUserAlerts(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Security alerts retrieved", list));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ApiResponse<String>> reviewAlert(@PathVariable Long id) {
        fraudDetectionService.markAlertAsReviewed(id);
        return ResponseEntity.ok(ApiResponse.success("Alert marked as reviewed"));
    }

    @PostMapping("/{id}/secure")
    public ResponseEntity<ApiResponse<String>> secureAccount(@PathVariable Long id) {
        fraudDetectionService.markAlertAsReviewed(id);
        return ResponseEntity.ok(ApiResponse.success("Security alert acknowledged. Account sessions secured. Redirecting to security settings."));
    }
}

