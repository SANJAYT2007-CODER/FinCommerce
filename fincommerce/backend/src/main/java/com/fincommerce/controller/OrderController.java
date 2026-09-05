package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.OrderDtos;
import com.fincommerce.entity.ReturnRefund;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDtos.OrderDto>>> getUserOrders(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OrderDtos.OrderDto> orders = orderService.getUserOrders(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orders));
    }

    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderDtos.OrderDto>> getOrderDetails(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String orderNumber) {
        OrderDtos.OrderDto order = orderService.getOrderDetails(userPrincipal.getId(), orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved", order));
    }

    @PostMapping("/returns")
    public ResponseEntity<ApiResponse<ReturnRefund>> requestReturn(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody OrderDtos.ReturnRequestDto request) {
        ReturnRefund refund = orderService.requestReturn(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Return requested and refund processed to wallet", refund));
    }
}
