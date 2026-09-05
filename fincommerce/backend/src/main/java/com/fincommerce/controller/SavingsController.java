package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.SavingsDtos;
import com.fincommerce.entity.SavingsGoal;
import com.fincommerce.entity.WalletTransaction;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.SavingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
public class SavingsController {

    @Autowired
    private SavingsService savingsService;

    @PostMapping("/goals")
    public ResponseEntity<ApiResponse<SavingsGoal>> createGoal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SavingsDtos.CreateGoalRequest request) {
        SavingsGoal goal = savingsService.createGoal(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Savings goal created successfully", goal));
    }

    @GetMapping("/goals")
    public ResponseEntity<ApiResponse<List<SavingsGoal>>> getGoals(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<SavingsGoal> goals = savingsService.getUserGoals(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Savings goals retrieved", goals));
    }

    @GetMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<SavingsGoal>> getGoalById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        SavingsGoal goal = savingsService.getGoalById(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Savings goal retrieved", goal));
    }

    @PutMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<SavingsGoal>> updateGoal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody SavingsDtos.UpdateGoalRequest request) {
        SavingsGoal updated = savingsService.updateGoal(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Savings goal updated successfully", updated));
    }

    @DeleteMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGoal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        savingsService.deleteGoal(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Savings goal deleted successfully"));
    }

    @PostMapping("/goals/{id}/add-money")
    public ResponseEntity<ApiResponse<SavingsGoal>> addMoney(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody SavingsDtos.AddMoneyRequest request) {
        SavingsGoal updatedGoal = savingsService.addMoneyToGoal(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Money moved to savings goal successfully", updatedGoal));
    }

    @PostMapping("/goals/{id}/withdraw")
    public ResponseEntity<ApiResponse<SavingsGoal>> withdraw(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody SavingsDtos.WithdrawRequest request) {
        SavingsGoal updatedGoal = savingsService.withdrawFromGoal(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Money withdrawn from savings goal successfully", updatedGoal));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SavingsDtos.SavingsSummaryDto>> getSavingsSummary(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        SavingsDtos.SavingsSummaryDto summary = savingsService.getSavingsSummary(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Savings summary retrieved", summary));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<WalletTransaction>>> getSavingsTransactions(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<WalletTransaction> transactions = savingsService.getSavingsTransactions(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Savings transactions retrieved", transactions));
    }
}
