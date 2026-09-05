package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.entity.WalletTransaction;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletTransaction>>> getTransactions(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        List<WalletTransaction> list = transactionService.getUserTransactions(userPrincipal.getId(), type, status, search);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", list));
    }
}
