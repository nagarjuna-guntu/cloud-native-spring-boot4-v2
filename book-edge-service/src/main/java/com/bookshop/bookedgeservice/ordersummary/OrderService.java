package com.bookshop.bookedgeservice.ordersummary;

import com.bookshop.bookedgeservice.clients.OrderServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderServiceClient orderServiceClient;

    public OrderService(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @TimeLimiter(name = "orderService")
    @Retry(name = "orderService")
    @CircuitBreaker(name = "orderService")
    public Mono<Order> findOrderById(Long orderId) {
        return orderServiceClient.findOrderById(orderId);
    }

    @TimeLimiter(name = "orderService")
    @Retry(name = "orderService")
    @CircuitBreaker(name = "orderService")
    public Flux<Order> getAllOrdersByUser() {
        return orderServiceClient.getAllOrdersByUser();
    }
}
