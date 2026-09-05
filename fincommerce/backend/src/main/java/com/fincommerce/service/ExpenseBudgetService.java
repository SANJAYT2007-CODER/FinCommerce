package com.fincommerce.service;

import com.fincommerce.dto.FinancialDtos;
import com.fincommerce.entity.Budget;
import com.fincommerce.entity.Expense;
import com.fincommerce.entity.User;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.BudgetRepository;
import com.fincommerce.repository.ExpenseRepository;
import com.fincommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpenseBudgetService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Expense> getUserExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
    }

    @Transactional
    public Expense addExpense(Long userId, FinancialDtos.AddExpenseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : LocalDate.now());
        expense.setDescription(request.getDescription());

        Expense savedExpense = expenseRepository.save(expense);

        // Check budget thresholds
        checkBudgetWarnings(userId, request.getCategory());

        return savedExpense;
    }

    @Transactional
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        if (!expense.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Expense not found for user");
        }
        expenseRepository.delete(expense);
    }

    public List<Budget> getUserBudgets(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Transactional
    public Budget setBudget(Long userId, FinancialDtos.SetBudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryAndMonthYear(
                userId, request.getCategory(), request.getMonthYear());

        Budget budget = existing.orElseGet(() -> {
            Budget b = new Budget();
            b.setUser(user);
            b.setCategory(request.getCategory());
            b.setMonthYear(request.getMonthYear());
            return b;
        });

        budget.setMonthlyLimit(request.getMonthlyLimit());
        return budgetRepository.save(budget);
    }

    public Map<String, Object> getFinancialSummary(Long userId) {
        List<Expense> expenses = getUserExpenses(userId);
        List<Budget> budgets = getUserBudgets(userId);

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryExpenses = new HashMap<>();
        for (Expense e : expenses) {
            categoryExpenses.put(e.getCategory(),
                    categoryExpenses.getOrDefault(e.getCategory(), BigDecimal.ZERO).add(e.getAmount()));
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExpenses", totalExpenses);
        summary.put("categoryExpenses", categoryExpenses);
        summary.put("budgets", budgets);
        return summary;
    }

    private void checkBudgetWarnings(Long userId, String category) {
        String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndCategoryAndMonthYear(userId, category, monthYear);

        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            BigDecimal categoryTotal = getUserExpenses(userId).stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (categoryTotal.compareTo(budget.getMonthlyLimit()) > 0) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    notificationService.createNotification(
                            user,
                            "Budget Warning: " + category,
                            "You have exceeded your monthly budget of ₹" + budget.getMonthlyLimit() + " for " + category + ". Current spending: ₹" + categoryTotal,
                            "BUDGET_EXCEEDED",
                            "settings.html"
                    );
                }
            }
        }
    }
}
