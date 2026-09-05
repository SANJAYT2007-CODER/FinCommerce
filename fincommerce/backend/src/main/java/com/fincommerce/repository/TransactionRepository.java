package com.fincommerce.repository;

import com.fincommerce.entity.User;
import com.fincommerce.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<WalletTransaction> findByUserOrderByCreatedAtDesc(User user);
    Optional<WalletTransaction> findByTransactionId(String transactionId);
    List<WalletTransaction> findByUserIdAndTypeInOrderByCreatedAtDesc(Long userId, List<String> types);
}


