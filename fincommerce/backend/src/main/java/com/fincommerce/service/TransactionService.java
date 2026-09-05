package com.fincommerce.service;

import com.fincommerce.entity.WalletTransaction;
import com.fincommerce.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<WalletTransaction> getUserTransactions(Long userId, String type, String status, String search) {
        List<WalletTransaction> list = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return list.stream()
                .filter(t -> (type == null || type.equalsIgnoreCase("ALL") || t.getType().equalsIgnoreCase(type)))
                .filter(t -> (status == null || status.equalsIgnoreCase("ALL") || t.getStatus().equalsIgnoreCase(status)))
                .filter(t -> (search == null || search.trim().isEmpty() ||
                        t.getTransactionId().toLowerCase().contains(search.toLowerCase()) ||
                        (t.getNote() != null && t.getNote().toLowerCase().contains(search.toLowerCase())) ||
                        (t.getSenderInfo() != null && t.getSenderInfo().toLowerCase().contains(search.toLowerCase())) ||
                        (t.getReceiverInfo() != null && t.getReceiverInfo().toLowerCase().contains(search.toLowerCase()))
                ))
                .collect(Collectors.toList());
    }

    public List<WalletTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
