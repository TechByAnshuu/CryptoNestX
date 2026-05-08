package com.cryptonest.exchange.controller;

import com.cryptonest.exchange.dto.WalletTopUpRequest;
import com.cryptonest.exchange.entity.Wallet;
import com.cryptonest.exchange.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/exchange/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @RequestHeader("X-User-Id") UUID userId) {
        Wallet wallet = walletService.getOrCreateWallet(userId);
        return ResponseEntity.ok(Map.of("balance", wallet.getBalance()));
    }

    @PostMapping("/topup")
    public ResponseEntity<Map<String, Object>> topUp(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody WalletTopUpRequest request) {
        Wallet wallet = walletService.topUp(userId, request.getAmount(), request.getPaymentMethodId());
        return ResponseEntity.ok(Map.of(
                "message", "Top up successful",
                "newBalance", wallet.getBalance()
        ));
    }
}
