package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.SupportDtos;
import com.fincommerce.entity.SupportTicket;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.SupportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Autowired
    private SupportService supportService;

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getTickets(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<SupportTicket> tickets = supportService.getUserTickets(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Tickets retrieved", tickets));
    }

    @PostMapping("/tickets")
    public ResponseEntity<ApiResponse<SupportTicket>> createTicket(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SupportDtos.CreateTicketRequest request) {
        SupportTicket ticket = supportService.createTicket(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Support ticket created", ticket));
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<SupportDtos.ChatQueryResponse>> chatQuery(@Valid @RequestBody SupportDtos.ChatQueryRequest request) {
        SupportDtos.ChatQueryResponse response = supportService.processChatbotQuery(request.getQuery());
        return ResponseEntity.ok(ApiResponse.success("Chat response", response));
    }
}
