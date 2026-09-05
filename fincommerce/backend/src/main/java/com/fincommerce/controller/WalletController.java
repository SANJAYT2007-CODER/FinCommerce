package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.WalletDtos;
import com.fincommerce.entity.WalletTransaction;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletDtos.WalletDto>> getWallet(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        WalletDtos.WalletDto wallet = walletService.getWalletDto(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Wallet details retrieved", wallet));
    }

    @PostMapping("/add-money")
    public ResponseEntity<ApiResponse<WalletTransaction>> addMoney(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody WalletDtos.AddMoneyRequest request) {
        WalletTransaction tx = walletService.addMoney(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Money added successfully", tx));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<WalletTransaction>> withdraw(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody WalletDtos.WithdrawRequest request) {
        WalletTransaction tx = walletService.withdrawMoney(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Money withdrawn successfully", tx));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<WalletTransaction>> sendMoney(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody WalletDtos.SendMoneyRequest request) {
        WalletTransaction tx = walletService.sendMoney(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Money sent successfully", tx));
    }

    @PostMapping("/pay-upi")
    public ResponseEntity<ApiResponse<WalletTransaction>> payUpi(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody WalletDtos.UpiPaymentRequest request) {
        WalletTransaction tx = walletService.payUpi(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("UPI payment successful", tx));
    }

    @GetMapping("/qr")
    public ResponseEntity<ApiResponse<WalletDtos.QRGenerateResponse>> getQR(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        WalletDtos.QRGenerateResponse qr = walletService.generateUserQR(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("QR code generated", qr));
    }
}
