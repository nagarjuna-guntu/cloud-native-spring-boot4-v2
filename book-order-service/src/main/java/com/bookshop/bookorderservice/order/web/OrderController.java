package com.bookshop.bookorderservice.order.web;

import com.bookshop.bookorderservice.order.domain.Order;
import com.bookshop.bookorderservice.order.domain.OrderService;
import com.bookshop.bookorderservice.order.domain.OrderStatus;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Iterable<OrderResponse> findOrdersByUser(@AuthenticationPrincipal Jwt jwt) {
        var userName = Objects.requireNonNullElse(jwt.getClaimAsString("name"), "Anonymous");
        log.info("Fetching all orders for logged in user {} ..", userName);
        return orderService.findOrdersByUser(userName);

    }

    @GetMapping("{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        log.info("Fetching order by id {} ..", id);
        return orderService.findOrderById(id);
    }

    @GetMapping("/status")
    public Map<OrderStatus, List<OrderResponse>> getOrdersByStatus() {
        return orderService.findOrdersByStatus();
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        log.info("Create order with request {}..", orderRequest);
        return orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity());

    }
}
