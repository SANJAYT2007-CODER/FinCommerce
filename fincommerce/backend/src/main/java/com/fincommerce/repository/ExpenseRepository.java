package com.fincommerce.repository;

import com.fincommerce.entity.Expense;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserOrderByExpenseDateDesc(User user);
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);
}
