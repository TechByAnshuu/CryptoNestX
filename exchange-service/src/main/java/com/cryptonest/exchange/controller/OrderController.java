package com.cryptonest.exchange.controller;

import com.cryptonest.exchange.dto.OrderResponse;
import com.cryptonest.exchange.dto.PlaceOrderRequest;
import com.cryptonest.exchange.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exchange/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(userId, userEmail, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getHistory(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }
}
