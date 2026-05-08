package com.cryptonest.exchange.service;

import com.cryptonest.exchange.dto.OrderResponse;
import com.cryptonest.exchange.dto.PlaceOrderRequest;
import com.cryptonest.exchange.entity.Order;
import com.cryptonest.exchange.entity.Transaction;
import com.cryptonest.exchange.kafka.OrderEventProducer;
import com.cryptonest.exchange.repository.OrderRepository;
import com.cryptonest.exchange.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public OrderResponse placeOrder(UUID userId, String userEmail, PlaceOrderRequest request) {
        BigDecimal totalCost = request.getPrice().multiply(request.getQuantity());
        BigDecimal fee = totalCost.multiply(new BigDecimal("0.001")); // 0.1% fee

        Order order = Order.builder()
                .userId(userId)
                .symbol(request.getSymbol().toUpperCase())
                .type(request.getType())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .status(Order.OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        try {
            if (request.getType() == Order.OrderType.BUY) {
                // Ensure sufficient funds including fee
                walletService.debit(userId, totalCost.add(fee));
                // In a real system, we'd make a synchronous call or emit an event to the Portfolio Service to add the holding.
            } else {
                // For SELL, we'd need to check if they have enough crypto in the portfolio service.
                // Assuming they do for this simulation, we credit their wallet
                walletService.credit(userId, totalCost.subtract(fee));
            }

            order.setStatus(Order.OrderStatus.EXECUTED);
            order = orderRepository.save(order);

            Transaction transaction = Transaction.builder()
                    .orderId(order.getId())
                    .userId(userId)
                    .amount(totalCost)
                    .fee(fee)
                    .build();
            transactionRepository.save(transaction);

            // Publish event so Notification service can send an email
            orderEventProducer.publishOrderEvent(order, userEmail);

        } catch (Exception e) {
            log.error("Order execution failed: {}", e.getMessage());
            order.setStatus(Order.OrderStatus.FAILED);
            orderRepository.save(order);
            throw new IllegalStateException("Order execution failed: " + e.getMessage());
        }

        return mapToResponse(order);
    }

    public List<OrderResponse> getOrderHistory(UUID userId) {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .symbol(order.getSymbol())
                .type(order.getType().name())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
