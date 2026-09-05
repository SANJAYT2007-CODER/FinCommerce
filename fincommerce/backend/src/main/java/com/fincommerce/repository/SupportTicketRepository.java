package com.fincommerce.repository;

import com.fincommerce.entity.SupportTicket;
import com.fincommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByUserOrderByCreatedAtDesc(User user);
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
}
