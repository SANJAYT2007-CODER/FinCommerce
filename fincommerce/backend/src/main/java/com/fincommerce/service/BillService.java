package com.fincommerce.service;

import com.fincommerce.dto.FinancialDtos;
import com.fincommerce.entity.*;
import com.fincommerce.exception.BadRequestException;
import com.fincommerce.exception.InsufficientBalanceException;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class BillService {

    @Autowired
    private BillPaymentRepository billPaymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    public List<BillPayment> getUserBillPayments(Long userId) {
        return billPaymentRepository.findByUserIdOrderByPaidAtDesc(userId);
    }

    @Transactional
    public BillPayment payBill(Long userId, FinancialDtos.BillPaymentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getTransactionPin() != null && request.getPin() != null && !user.getTransactionPin().equals(request.getPin())) {
            throw new BadRequestException("Invalid Transaction PIN");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        BigDecimal amount = request.getAmount();
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance for bill payment");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setTotalSpent(wallet.getTotalSpent().add(amount));
        wallet.setMonthlySpent(wallet.getMonthlySpent().add(amount));
        walletRepository.save(wallet);

        String refId = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BillPayment bill = new BillPayment();
        bill.setUser(user);
        bill.setCategory(request.getCategory());
        bill.setProviderName(request.getProviderName());
        bill.setCustomerNumber(request.getCustomerNumber());
        bill.setAmount(amount);
        bill.setTransactionRef(refId);
        bill.setStatus("SUCCESS");

        BillPayment savedBill = billPaymentRepository.save(bill);

        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionId(refId);
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setType("BILL_PAYMENT");
        tx.setAmount(amount);
        tx.setPaymentMethod("WALLET");
        tx.setStatus("SUCCESS");
        tx.setNote(request.getCategory() + " Bill Payment to " + request.getProviderName());
        tx.setSenderInfo(user.getFullName() + " Wallet");
        tx.setReceiverInfo(request.getProviderName() + " (" + request.getCustomerNumber() + ")");
        tx.setReferenceId(savedBill.getId().toString());

        WalletTransaction savedTx = transactionRepository.save(tx);

        notificationService.createNotification(
                user,
                "Bill Paid Successfully",
                "₹" + amount + " paid for " + request.getCategory() + " (" + request.getProviderName() + ")",
                "PAYMENT_SUCCESS",
                "/transactions.html"
        );

        fraudDetectionService.checkTransaction(savedTx);

        return savedBill;
    }
}
