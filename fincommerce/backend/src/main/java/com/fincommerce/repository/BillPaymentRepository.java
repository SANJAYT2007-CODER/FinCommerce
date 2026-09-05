package com.fincommerce.repository;

import com.fincommerce.entity.BillPayment;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    List<BillPayment> findByUserOrderByPaidAtDesc(User user);
    List<BillPayment> findByUserIdOrderByPaidAtDesc(Long userId);
}
