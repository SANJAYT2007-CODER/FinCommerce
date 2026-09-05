package com.fincommerce.repository;

import com.fincommerce.entity.SecurityAlert;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, Long> {
    List<SecurityAlert> findByUserOrderByCreatedAtDesc(User user);
    List<SecurityAlert> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<SecurityAlert> findByIsReviewedFalseOrderByCreatedAtDesc();
    List<SecurityAlert> findAllByOrderByCreatedAtDesc();
}
