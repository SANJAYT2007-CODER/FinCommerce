package com.fincommerce.repository;

import com.fincommerce.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SavingsGoal> findByUserIdAndStatus(Long userId, String status);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, String status);

    @Query("SELECT COALESCE(SUM(s.savedAmount), 0) FROM SavingsGoal s WHERE s.user.id = :userId")
    BigDecimal sumSavedAmountByUserId(@Param("userId") Long userId);
}
