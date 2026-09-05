package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.FinancialDtos;
import com.fincommerce.entity.BillPayment;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.BillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillPayment>>> getBillPayments(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<BillPayment> list = billService.getUserBillPayments(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Bill payments retrieved", list));
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<BillPayment>> payBill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FinancialDtos.BillPaymentRequest request) {
        BillPayment bill = billService.payBill(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Bill paid successfully", bill));
    }
}
