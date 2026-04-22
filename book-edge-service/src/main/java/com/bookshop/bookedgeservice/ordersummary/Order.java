package com.bookshop.bookedgeservice.ordersummary;

import org.springframework.data.annotation.Version;

import java.time.Instant;

public record Order(
        Long id,
        String bookIsbn,
        String bookName,
        Double orderTotal,
        int quantity,
        String status,
        Instant createdDate,
        String createdBy,
        String reason
) {
}
