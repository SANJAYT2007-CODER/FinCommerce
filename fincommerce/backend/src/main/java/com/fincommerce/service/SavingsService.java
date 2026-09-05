package com.fincommerce.service;

import com.fincommerce.dto.SavingsDtos;
import com.fincommerce.entity.SavingsGoal;
import com.fincommerce.entity.User;
import com.fincommerce.entity.Wallet;
import com.fincommerce.entity.WalletTransaction;
import com.fincommerce.exception.BadRequestException;
import com.fincommerce.exception.InsufficientBalanceException;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.SavingsGoalRepository;
import com.fincommerce.repository.TransactionRepository;
import com.fincommerce.repository.UserRepository;
import com.fincommerce.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class SavingsService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WalletService walletService;

    @Transactional
    public SavingsGoal createGoal(Long userId, SavingsDtos.CreateGoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        goal.setGoalName(request.getGoalName());
        goal.setCategory(request.getCategory());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
        goal.setSavedAmount(BigDecimal.ZERO);
        goal.setStatus("ACTIVE");

        BigDecimal initialAmount = request.getInitialSavingAmount() != null ? request.getInitialSavingAmount() : BigDecimal.ZERO;

        if (initialAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Check Transaction PIN
            if (user.getTransactionPin() != null && !user.getTransactionPin().equals(request.getPin())) {
                throw new BadRequestException("Invalid Transaction PIN");
            }

            Wallet wallet = walletService.getWalletByUserId(userId);
            if (wallet.getAvailableBalance().compareTo(initialAmount) < 0) {
                throw new InsufficientBalanceException("Insufficient wallet balance for initial saving");
            }

            // Deduct from wallet
            wallet.setBalance(wallet.getBalance().subtract(initialAmount));
            wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(initialAmount));
            walletRepository.save(wallet);

            goal.setSavedAmount(initialAmount);

            if (initialAmount.compareTo(request.getTargetAmount()) >= 0) {
                goal.setStatus("COMPLETED");
            }

            SavingsGoal savedGoal = savingsGoalRepository.save(goal);

            // Record transaction
            recordTransaction(user, wallet, "SAVINGS_TRANSFER", initialAmount, 
                    "Initial deposit to savings goal: " + savedGoal.getGoalName(), savedGoal.getGoalName());

            notificationService.createNotification(
                    user,
                    "Savings Goal Created",
                    "₹" + initialAmount + " moved to your new savings goal: " + savedGoal.getGoalName(),
                    "SAVINGS_TRANSFER",
                    "savings.html"
            );

            if ("COMPLETED".equals(savedGoal.getStatus())) {
                notificationService.createNotification(
                        user,
                        "🎉 Goal Reached!",
                        "Congratulations! You reached your savings goal for " + savedGoal.getGoalName(),
                        "GOAL_COMPLETED",
                        "savings.html"
                );
            }

            return savedGoal;
        }

        return savingsGoalRepository.save(goal);
    }

    public List<SavingsGoal> getUserGoals(Long userId) {
        return savingsGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public SavingsGoal getGoalById(Long userId, Long goalId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found"));
        if (!goal.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to savings goal");
        }
        return goal;
    }

    @Transactional
    public SavingsGoal updateGoal(Long userId, Long goalId, SavingsDtos.UpdateGoalRequest request) {
        SavingsGoal goal = getGoalById(userId, goalId);

        if (request.getGoalName() != null) goal.setGoalName(request.getGoalName());
        if (request.getCategory() != null) goal.setCategory(request.getCategory());
        if (request.getTargetAmount() != null) goal.setTargetAmount(request.getTargetAmount());
        if (request.getTargetDate() != null) goal.setTargetDate(request.getTargetDate());
        if (request.getDescription() != null) goal.setDescription(request.getDescription());

        if (goal.getSavedAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("COMPLETED");
        } else {
            goal.setStatus("ACTIVE");
        }

        return savingsGoalRepository.save(goal);
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        SavingsGoal goal = getGoalById(userId, goalId);
        // If money is saved, return to wallet automatically before delete
        if (goal.getSavedAmount().compareTo(BigDecimal.ZERO) > 0) {
            Wallet wallet = walletService.getWalletByUserId(userId);
            wallet.setBalance(wallet.getBalance().add(goal.getSavedAmount()));
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(goal.getSavedAmount()));
            walletRepository.save(wallet);

            recordTransaction(goal.getUser(), wallet, "SAVINGS_WITHDRAWAL", goal.getSavedAmount(),
                    "Refund of saved amount from deleted goal: " + goal.getGoalName(), wallet.getUser().getFullName() + " Wallet");
        }
        savingsGoalRepository.delete(goal);
    }

    @Transactional
    public SavingsGoal addMoneyToGoal(Long userId, Long goalId, SavingsDtos.AddMoneyRequest request) {
        SavingsGoal goal = getGoalById(userId, goalId);
        User user = goal.getUser();

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        if (user.getTransactionPin() != null && !user.getTransactionPin().equals(request.getPin())) {
            throw new BadRequestException("Invalid Transaction PIN");
        }

        Wallet wallet = walletService.getWalletByUserId(userId);
        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance to add to savings");
        }

        // Deduct from wallet
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        // Add to goal
        BigDecimal newSavedAmount = goal.getSavedAmount().add(request.getAmount());
        goal.setSavedAmount(newSavedAmount);

        boolean justCompleted = false;
        if (newSavedAmount.compareTo(goal.getTargetAmount()) >= 0 && !"COMPLETED".equals(goal.getStatus())) {
            goal.setStatus("COMPLETED");
            justCompleted = true;
        }

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);

        recordTransaction(user, wallet, "SAVINGS_TRANSFER", request.getAmount(),
                "Moved to savings goal: " + goal.getGoalName(), goal.getGoalName());

        notificationService.createNotification(
                user,
                "Money Saved!",
                "₹" + request.getAmount() + " moved to your " + goal.getGoalName() + " savings goal.",
                "SAVINGS_TRANSFER",
                "savings.html"
        );

        if (justCompleted) {
            notificationService.createNotification(
                    user,
                    "🎉 Goal Reached!",
                    "Congratulations! You reached your savings goal for " + goal.getGoalName(),
                    "GOAL_COMPLETED",
                    "savings.html"
            );
        }

        return updatedGoal;
    }

    @Transactional
    public SavingsGoal withdrawFromGoal(Long userId, Long goalId, SavingsDtos.WithdrawRequest request) {
        SavingsGoal goal = getGoalById(userId, goalId);
        User user = goal.getUser();

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Withdrawal amount must be greater than 0");
        }

        if (user.getTransactionPin() != null && !user.getTransactionPin().equals(request.getPin())) {
            throw new BadRequestException("Invalid Transaction PIN");
        }

        if (request.getAmount().compareTo(goal.getSavedAmount()) > 0) {
            throw new BadRequestException("Withdrawal amount cannot exceed saved amount");
        }

        // Deduct from goal
        BigDecimal newSavedAmount = goal.getSavedAmount().subtract(request.getAmount());
        goal.setSavedAmount(newSavedAmount);
        if (newSavedAmount.compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus("ACTIVE");
        }

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);

        // Add to wallet
        Wallet wallet = walletService.getWalletByUserId(userId);
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        recordTransaction(user, wallet, "SAVINGS_WITHDRAWAL", request.getAmount(),
                "Withdrawn from savings goal: " + goal.getGoalName(), user.getFullName() + " Wallet");

        notificationService.createNotification(
                user,
                "Savings Withdrawal",
                "₹" + request.getAmount() + " withdrawn from your savings and added to your wallet.",
                "SAVINGS_WITHDRAWAL",
                "savings.html"
        );

        return updatedGoal;
    }

    public SavingsDtos.SavingsSummaryDto getSavingsSummary(Long userId) {
        BigDecimal totalSaved = savingsGoalRepository.sumSavedAmountByUserId(userId);
        long totalGoals = savingsGoalRepository.countByUserId(userId);
        long activeGoals = savingsGoalRepository.countByUserIdAndStatus(userId, "ACTIVE");
        long completedGoals = savingsGoalRepository.countByUserIdAndStatus(userId, "COMPLETED");

        Wallet wallet = walletService.getWalletByUserId(userId);

        List<SavingsGoal> allGoals = getUserGoals(userId);
        BigDecimal totalTarget = BigDecimal.ZERO;
        for (SavingsGoal g : allGoals) {
            totalTarget = totalTarget.add(g.getTargetAmount());
        }

        double progressPct = 0.0;
        if (totalTarget.compareTo(BigDecimal.ZERO) > 0) {
            progressPct = totalSaved.divide(totalTarget, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        }

        SavingsDtos.SavingsSummaryDto summary = new SavingsDtos.SavingsSummaryDto();
        summary.setTotalSavedAmount(totalSaved != null ? totalSaved : BigDecimal.ZERO);
        summary.setTotalSavingsGoals(totalGoals);
        summary.setActiveSavingsGoals(activeGoals);
        summary.setCompletedGoals(completedGoals);
        summary.setAvailableWalletBalance(wallet.getAvailableBalance());
        summary.setMonthlySavings(totalSaved); // Default monthly view
        summary.setSavingsProgress(Math.min(100.0, Math.round(progressPct * 10.0) / 10.0));

        return summary;
    }

    public List<WalletTransaction> getSavingsTransactions(Long userId) {
        return transactionRepository.findByUserIdAndTypeInOrderByCreatedAtDesc(
                userId, Arrays.asList("SAVINGS_TRANSFER", "SAVINGS_WITHDRAWAL")
        );
    }

    private void recordTransaction(User user, Wallet wallet, String type, BigDecimal amount, String note, String receiverInfo) {
        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionId("TXN-SVG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setPaymentMethod("SAVINGS");
        tx.setStatus("SUCCESS");
        tx.setNote(note);
        tx.setSenderInfo(type.equals("SAVINGS_TRANSFER") ? user.getFullName() + " Wallet" : "Savings Goal");
        tx.setReceiverInfo(receiverInfo);
        transactionRepository.save(tx);
    }
}
