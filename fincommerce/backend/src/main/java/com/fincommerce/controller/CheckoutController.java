package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.CheckoutDtos;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckoutDtos.OrderConfirmationResponse>> checkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CheckoutDtos.CheckoutRequest request) {
        CheckoutDtos.OrderConfirmationResponse response = orderService.processCheckout(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Order processed successfully", response));
    }
}
