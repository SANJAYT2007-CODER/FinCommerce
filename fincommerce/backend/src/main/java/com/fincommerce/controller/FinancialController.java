package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.FinancialDtos;
import com.fincommerce.entity.Budget;
import com.fincommerce.entity.Expense;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.ExpenseBudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FinancialController {

    @Autowired
    private ExpenseBudgetService expenseBudgetService;

    @GetMapping("/expenses")
    public ResponseEntity<ApiResponse<List<Expense>>> getExpenses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Expense> expenses = expenseBudgetService.getUserExpenses(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved", expenses));
    }

    @PostMapping("/expenses")
    public ResponseEntity<ApiResponse<Expense>> addExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FinancialDtos.AddExpenseRequest request) {
        Expense expense = expenseBudgetService.addExpense(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Expense added successfully", expense));
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        expenseBudgetService.deleteExpense(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }

    @GetMapping("/budgets")
    public ResponseEntity<ApiResponse<List<Budget>>> getBudgets(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Budget> budgets = expenseBudgetService.getUserBudgets(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved", budgets));
    }

    @PostMapping("/budgets")
    public ResponseEntity<ApiResponse<Budget>> setBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FinancialDtos.SetBudgetRequest request) {
        Budget budget = expenseBudgetService.setBudget(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", budget));
    }

    @GetMapping("/financial-summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFinancialSummary(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> summary = expenseBudgetService.getFinancialSummary(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Financial summary retrieved", summary));
    }
}
