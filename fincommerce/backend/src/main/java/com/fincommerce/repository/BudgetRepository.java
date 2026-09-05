package com.fincommerce.repository;

import com.fincommerce.entity.Budget;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserId(Long userId);
    Optional<Budget> findByUserIdAndCategoryAndMonthYear(Long userId, String category, String monthYear);
}
