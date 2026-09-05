package com.fincommerce.service;

import com.fincommerce.entity.SecurityAlert;
import com.fincommerce.entity.WalletTransaction;
import com.fincommerce.repository.SecurityAlertRepository;
import com.fincommerce.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FraudDetectionService {

    @Autowired
    private SecurityAlertRepository securityAlertRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void checkTransaction(WalletTransaction transaction) {
        boolean isSuspicious = false;
        String reason = "";

        // Rule 1: High transaction amount threshold (₹20,000)
        if (transaction.getAmount().compareTo(new BigDecimal("20000.00")) > 0) {
            isSuspicious = true;
            reason = "Unusually large transaction amount (₹" + transaction.getAmount() + ")";
        }

        if (isSuspicious) {
            transaction.setIsSuspicious(true);
            transactionRepository.save(transaction);

            SecurityAlert alert = new SecurityAlert();
            alert.setUser(transaction.getUser());
            alert.setTransactionId(transaction.getTransactionId());
            alert.setAlertMessage("Suspicious transaction detected: " + reason + ". Please verify this payment.");
            alert.setSeverity("HIGH");
            alert.setIsReviewed(false);
            alert.setStatus("DETECTED");
            securityAlertRepository.save(alert);

            notificationService.createNotification(
                    transaction.getUser(),
                    "Security Alert: Suspicious Transaction Detected",
                    "Suspicious transaction detected (" + transaction.getTransactionId() + "). Please verify this payment in your Security Center.",
                    "SUSPICIOUS_ALERT",
                    "settings.html"
            );
        }
    }

    public List<SecurityAlert> getUserAlerts(Long userId) {
        return securityAlertRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<SecurityAlert> getAllAlerts() {
        return securityAlertRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void markAlertAsReviewed(Long alertId) {
        securityAlertRepository.findById(alertId).ifPresent(alert -> {
            alert.setIsReviewed(true);
            alert.setStatus("REVIEWED");
            securityAlertRepository.save(alert);
        });
    }
}
