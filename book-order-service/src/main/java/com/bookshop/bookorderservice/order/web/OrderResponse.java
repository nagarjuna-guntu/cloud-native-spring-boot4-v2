package com.bookshop.bookorderservice.order.web;

import com.bookshop.bookorderservice.order.domain.OrderStatus;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String bookIsbn,
        String bookName,
        Double orderTotal,
        int quantity,
        OrderStatus status,
        Instant createdDate,
        String createdBy,
        String reason) {
}
