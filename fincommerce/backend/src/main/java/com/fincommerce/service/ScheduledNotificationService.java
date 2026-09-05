package com.fincommerce.service;

import com.fincommerce.entity.User;
import com.fincommerce.entity.Wallet;
import com.fincommerce.repository.SavingsGoalRepository;
import com.fincommerce.repository.UserRepository;
import com.fincommerce.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ScheduledNotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private NotificationService notificationService;

    // Run every 10 minutes to check user notification preferences and send scheduled alerts
    @Scheduled(fixedRate = 600000)
    public void processScheduledAlerts() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            try {
                // 1. Daily Financial Summary
                if (Boolean.TRUE.equals(user.getDailySummaryEnabled())) {
                    Wallet wallet = walletRepository.findByUserId(user.getId()).orElse(null);
                    BigDecimal walletBal = wallet != null ? wallet.getAvailableBalance() : BigDecimal.ZERO;
                    BigDecimal totalSaved = savingsGoalRepository.sumSavedAmountByUserId(user.getId());
                    BigDecimal monthlySpent = wallet != null ? wallet.getMonthlySpent() : BigDecimal.ZERO;

                    notificationService.createNotification(
                            user,
                            "🔔 Daily Financial Summary",
                            "Your wallet balance is ₹" + walletBal + ". You saved ₹" + (totalSaved != null ? totalSaved : "0.00") + " so far. Your monthly spending is ₹" + monthlySpent + ".",
                            "DAILY_SUMMARY",
                            "dashboard.html"
                    );
                }

                // 2. Daily Savings Reminder
                if (Boolean.TRUE.equals(user.getSavingsReminderEnabled())) {
                    notificationService.createNotification(
                            user,
                            "💰 Savings Reminder",
                            "You planned to save today! Would you like to move money to your savings goal?",
                            "SAVINGS_REMINDER",
                            "savings.html"
                    );
                }
            } catch (Exception e) {
                // Log and continue
                System.err.println("Scheduled notification error for user " + user.getId() + ": " + e.getMessage());
            }
        }
    }
}
