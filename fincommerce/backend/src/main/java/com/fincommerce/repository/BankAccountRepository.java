package com.fincommerce.repository;

import com.fincommerce.entity.BankAccount;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUser(User user);
    List<BankAccount> findByUserId(Long userId);
}
