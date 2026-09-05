package com.fincommerce.repository;

import com.fincommerce.entity.ReturnRefund;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRefundRepository extends JpaRepository<ReturnRefund, Long> {
    List<ReturnRefund> findByUserOrderByCreatedAtDesc(User user);
    List<ReturnRefund> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ReturnRefund> findAllByOrderByCreatedAtDesc();
}
