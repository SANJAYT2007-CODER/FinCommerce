package com.fincommerce.service;

import com.fincommerce.dto.SupportDtos;
import com.fincommerce.entity.SupportTicket;
import com.fincommerce.entity.User;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.SupportTicketRepository;
import com.fincommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SupportService {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private UserRepository userRepository;

    public List<SupportTicket> getUserTickets(Long userId) {
        return supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public SupportTicket createTicket(Long userId, SupportDtos.CreateTicketRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        ticket.setUser(user);
        ticket.setSubject(request.getSubject());
        ticket.setCategory(request.getCategory());
        ticket.setMessage(request.getMessage());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        ticket.setStatus("OPEN");

        return supportTicketRepository.save(ticket);
    }

    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public SupportTicket updateTicketStatus(Long ticketId, String status, String adminNotes) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));
        ticket.setStatus(status);
        if (adminNotes != null) {
            ticket.setAdminNotes(adminNotes);
        }
        return supportTicketRepository.save(ticket);
    }

    public SupportDtos.ChatQueryResponse processChatbotQuery(String query) {
        String q = query.toLowerCase();

        if (q.contains("add money") || q.contains("deposit") || q.contains("recharge wallet")) {
            return new SupportDtos.ChatQueryResponse(
                    "To add money to your wallet, go to the Wallet section, click 'Add Money', enter the desired amount, and select your payment method (UPI, Debit Card, Credit Card, or Net Banking).",
                    "WALLET",
                    "wallet.html"
            );
        } else if (q.contains("refund") || q.contains("return")) {
            return new SupportDtos.ChatQueryResponse(
                    "You can request a return on any delivered order under 'My Orders'. Once approved, refunds are credited instantly to your FinCommerce wallet balance!",
                    "REFUND",
                    "orders.html"
            );
        } else if (q.contains("pin") || q.contains("security")) {
            return new SupportDtos.ChatQueryResponse(
                    "You can update your Transaction PIN and manage security options under Settings -> Security Settings.",
                    "SECURITY",
                    "settings.html"
            );
        } else if (q.contains("track") || q.contains("delivery") || q.contains("where is my order")) {
            return new SupportDtos.ChatQueryResponse(
                    "To track your order, visit the Orders page. Each active order includes a visual live status timeline from Placed to Delivered.",
                    "ORDER",
                    "orders.html"
            );
        } else if (q.contains("coupon") || q.contains("discount") || q.contains("offer")) {
            return new SupportDtos.ChatQueryResponse(
                    "You can use coupons like 'WELCOME10' (10% OFF) or 'SAVE500' (₹500 OFF on orders over ₹5,000) during Cart Checkout!",
                    "COUPON",
                    "cart.html"
            );
        } else {
            return new SupportDtos.ChatQueryResponse(
                    "I am FinCommerce Assistant! I can help you with Wallet balance, UPI payments, order tracking, returns, coupons, and security settings. How can I assist you today?",
                    "GENERAL",
                    "support.html"
            );
        }
    }
}
