package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.FinancialDtos;
import com.fincommerce.entity.BankAccount;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.BankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class BankController {

    @Autowired
    private BankService bankService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BankAccount>>> getBankAccounts(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<BankAccount> list = bankService.getBankAccounts(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Bank accounts retrieved", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BankAccount>> addBankAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FinancialDtos.AddBankAccountRequest request) {
        BankAccount account = bankService.addBankAccount(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Bank account linked successfully", account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBankAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        bankService.deleteBankAccount(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Bank account removed"));
    }
}
