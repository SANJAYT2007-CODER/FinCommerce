package com.fincommerce.repository;

import com.fincommerce.entity.Payment;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
}
