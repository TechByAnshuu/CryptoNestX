package com.cryptonest.exchange.controller;

import com.cryptonest.exchange.dto.WalletBalanceDTO;
import com.cryptonest.exchange.dto.WalletFundRequest;
import com.cryptonest.exchange.dto.WalletTopUpRequest;
import com.cryptonest.exchange.entity.Wallet;
import com.cryptonest.exchange.repository.WalletTransactionRepository;
import com.cryptonest.exchange.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Deposit, withdrawal, and balance endpoints")
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionRepository walletTxnRepository;

    @PostMapping("/deposit")
    @Operation(summary = "Simulate a deposit into the user's wallet (max $100,000)")
    public ResponseEntity<WalletBalanceDTO> deposit(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody WalletFundRequest req) {
        walletService.deposit(userId, req.getAmount());
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Simulate a withdrawal from the user's wallet")
    public ResponseEntity<WalletBalanceDTO> withdraw(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody WalletFundRequest req) {
        walletService.withdraw(userId, req.getAmount());
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @GetMapping("/balance")
    @Operation(summary = "Get current wallet balance, currency, and last updated timestamp")
    public ResponseEntity<WalletBalanceDTO> balance(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get paginated deposit/withdrawal history")
    public ResponseEntity<?> history(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                walletTxnRepository.findByWalletUserIdOrderByCreatedAtDesc(
                        userId, PageRequest.of(page, size)));
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
