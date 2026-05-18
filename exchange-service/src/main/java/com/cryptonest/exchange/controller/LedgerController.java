package com.cryptonest.exchange.controller;

import com.cryptonest.exchange.dto.LedgerDTO;
import com.cryptonest.exchange.dto.LedgerSummaryDTO;
import com.cryptonest.exchange.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "Transaction Ledger", description = "Immutable audit trail of every trade")
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping
    @Operation(summary = "Get paginated trade history for the authenticated user")
    public ResponseEntity<Page<LedgerDTO>> getHistory(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ledgerService.getUserHistory(userId, page, size));
    }

    @GetMapping("/range")
    @Operation(summary = "Get trades within a date range")
    public ResponseEntity<List<LedgerDTO>> getHistoryByRange(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam Instant from,
            @RequestParam Instant to) {
        return ResponseEntity.ok(ledgerService.getUserHistoryByDateRange(userId, from, to));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get aggregate summary: total trades, volume, and fees paid")
    public ResponseEntity<LedgerSummaryDTO> getSummary(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ledgerService.getSummary(userId));
    }
}
