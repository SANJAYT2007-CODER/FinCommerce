package com.fincommerce.service;

import com.fincommerce.dto.WalletDtos;
import com.fincommerce.entity.*;
import com.fincommerce.exception.BadRequestException;
import com.fincommerce.exception.InsufficientBalanceException;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Wallet wallet = new Wallet(user, new BigDecimal("25450.00"));
                    return walletRepository.save(wallet);
                });
    }

    public WalletDtos.WalletDto getWalletDto(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        WalletDtos.WalletDto dto = new WalletDtos.WalletDto();
        dto.setWalletId(wallet.getId());
        dto.setBalance(wallet.getBalance());
        dto.setAvailableBalance(wallet.getAvailableBalance());
        dto.setTotalSpent(wallet.getTotalSpent());
        dto.setMonthlySpent(wallet.getMonthlySpent());
        dto.setUserFullName(wallet.getUser().getFullName());
        dto.setUserMobile(wallet.getUser().getMobileNumber());
        return dto;
    }

    @Transactional
    public WalletTransaction addMoney(Long userId, WalletDtos.AddMoneyRequest request) {
        Wallet wallet = getWalletByUserId(userId);
        User user = wallet.getUser();

        BigDecimal amount = request.getAmount();
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionId("TXN-ADD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setType("ADD_MONEY");
        tx.setAmount(amount);
        tx.setPaymentMethod(request.getPaymentMethod());
        tx.setStatus("SUCCESS");
        tx.setNote("Added funds via " + request.getPaymentMethod());
        tx.setSenderInfo(request.getPaymentMethod() + " Gateway");
        tx.setReceiverInfo(user.getFullName() + " Wallet");

        WalletTransaction savedTx = transactionRepository.save(tx);

        notificationService.createNotification(
                user,
                "Money Added Successfully",
                "₹" + amount + " added to your FinCommerce Wallet via " + request.getPaymentMethod(),
                "PAYMENT_SUCCESS",
                "wallet.html"
        );

        fraudDetectionService.checkTransaction(savedTx);

        return savedTx;
    }

    @Transactional
    public WalletTransaction withdrawMoney(Long userId, WalletDtos.WithdrawRequest request) {
        Wallet wallet = getWalletByUserId(userId);
        User user = wallet.getUser();

        if (user.getTransactionPin() != null && !user.getTransactionPin().equals(request.getPin())) {
            throw new BadRequestException("Invalid Transaction PIN");
        }

        BigDecimal amount = request.getAmount();
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance for withdrawal");
        }

        BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank Account not found"));

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setTotalSpent(wallet.getTotalSpent().add(amount));
        wallet.setMonthlySpent(wallet.getMonthlySpent().add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionId("TXN-WTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setType("WITHDRAW");
        tx.setAmount(amount);
        tx.setPaymentMethod("BANK_TRANSFER");
        tx.setStatus("SUCCESS");
        tx.setNote("Withdrawal to " + bankAccount.getBankName() + " (" + bankAccount.getMaskedAccountNumber() + ")");
        tx.setSenderInfo(user.getFullName() + " Wallet");
        tx.setReceiverInfo(bankAccount.getBankName() + " A/C " + bankAccount.getMaskedAccountNumber());

        WalletTransaction savedTx = transactionRepository.save(tx);

        notificationService.createNotification(
                user,
                "Withdrawal Successful",
                "₹" + amount + " transferred to " + bankAccount.getBankName(),
                "PAYMENT_SUCCESS",
                "wallet.html"
        );

        fraudDetectionService.checkTransaction(savedTx);

        return savedTx;
    }

    @Transactional
    public WalletTransaction sendMoney(Long userId, WalletDtos.SendMoneyRequest request) {
        Wallet senderWallet = getWalletByUserId(userId);
        User sender = senderWallet.getUser();

        if (sender.getTransactionPin() != null && !sender.getTransactionPin().equals(request.getPin())) {
            throw new BadRequestException("Invalid Security PIN");
        }

        BigDecimal amount = request.getAmount();
        if (senderWallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        String identifier = request.getReceiverIdentifier().trim();
        Optional<User> receiverOpt = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByMobileNumber(identifier));

        User receiver;
        if (receiverOpt.isPresent()) {
            receiver = receiverOpt.get();
        } else {
            // Mock receiver for non-registered email/UPI demo
            receiver = null;
        }

        if (receiver != null && receiver.getId().equals(userId)) {
            throw new BadRequestException("Cannot send money to yourself");
        }

        // Deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        senderWallet.setAvailableBalance(senderWallet.getAvailableBalance().subtract(amount));
        senderWallet.setTotalSpent(senderWallet.getTotalSpent().add(amount));
        senderWallet.setMonthlySpent(senderWallet.getMonthlySpent().add(amount));
        walletRepository.save(senderWallet);

        String txId = "TXN-SND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Credit receiver if registered user
        if (receiver != null) {
            Wallet receiverWallet = getWalletByUserId(receiver.getId());
            receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
            receiverWallet.setAvailableBalance(receiverWallet.getAvailableBalance().add(amount));
            walletRepository.save(receiverWallet);

            // Record receiver transaction
            WalletTransaction rTx = new WalletTransaction();
            rTx.setTransactionId("TXN-RCV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            rTx.setWallet(receiverWallet);
            rTx.setUser(receiver);
            rTx.setType("RECEIVE");
            rTx.setAmount(amount);
            rTx.setPaymentMethod("WALLET");
            rTx.setStatus("SUCCESS");
            rTx.setNote(request.getNote() != null ? request.getNote() : "Received from " + sender.getFullName());
            rTx.setSenderInfo(sender.getFullName() + " (" + sender.getMobileNumber() + ")");
            rTx.setReceiverInfo(receiver.getFullName() + " Wallet");
            transactionRepository.save(rTx);

            notificationService.createNotification(
                    receiver,
                    "Money Received!",
                    "You received ₹" + amount + " from " + sender.getFullName(),
                    "MONEY_RECEIVED",
                    "wallet.html"
            );
        }

        // Record sender transaction
        WalletTransaction sTx = new WalletTransaction();
        sTx.setTransactionId(txId);
        sTx.setWallet(senderWallet);
        sTx.setUser(sender);
        sTx.setType("SEND");
        sTx.setAmount(amount);
        sTx.setPaymentMethod("WALLET");
        sTx.setStatus("SUCCESS");
        sTx.setNote(request.getNote() != null ? request.getNote() : "Money Sent");
        sTx.setSenderInfo(sender.getFullName());
        sTx.setReceiverInfo(receiver != null ? receiver.getFullName() + " (" + receiver.getMobileNumber() + ")" : identifier);

        WalletTransaction savedTx = transactionRepository.save(sTx);

        notificationService.createNotification(
                sender,
                "Money Sent Successfully",
                "₹" + amount + " sent to " + (receiver != null ? receiver.getFullName() : identifier),
                "MONEY_SENT",
                "wallet.html"
        );

        fraudDetectionService.checkTransaction(savedTx);

        return savedTx;
    }

    @Transactional
    public WalletTransaction payUpi(Long userId, WalletDtos.UpiPaymentRequest request) {
        Wallet wallet = getWalletByUserId(userId);
        User user = wallet.getUser();

        if (user.getTransactionPin() != null && request.getPin() != null && !user.getTransactionPin().equals(request.getPin())) {
            throw new BadRequestException("Invalid Transaction PIN");
        }

        BigDecimal amount = request.getAmount();
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance for UPI payment");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setTotalSpent(wallet.getTotalSpent().add(amount));
        wallet.setMonthlySpent(wallet.getMonthlySpent().add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionId("TXN-UPI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setType("UPI_PAYMENT");
        tx.setAmount(amount);
        tx.setPaymentMethod("UPI");
        tx.setStatus("SUCCESS");
        tx.setNote("UPI Payment to " + request.getUpiId());
        tx.setSenderInfo(user.getFullName() + " Wallet");
        tx.setReceiverInfo(request.getUpiId());

        WalletTransaction savedTx = transactionRepository.save(tx);

        notificationService.createNotification(
                user,
                "UPI Payment Successful",
                "₹" + amount + " paid via UPI to " + request.getUpiId(),
                "PAYMENT_SUCCESS",
                "/wallet.html"
        );

        fraudDetectionService.checkTransaction(savedTx);

        return savedTx;
    }

    public WalletDtos.QRGenerateResponse generateUserQR(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String upiId = user.getMobileNumber() + "@fincommerce";
        String qrDataUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=upi://pay?pa=" + upiId + "&pn=" + user.getFullName().replace(" ", "%20");
        String shareLink = "https://fincommerce.com/pay?upi=" + upiId;

        return new WalletDtos.QRGenerateResponse(upiId, qrDataUrl, shareLink);
    }
}
