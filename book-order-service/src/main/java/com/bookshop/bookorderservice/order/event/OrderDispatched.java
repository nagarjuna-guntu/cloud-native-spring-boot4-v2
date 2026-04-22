package com.bookshop.bookorderservice.order.event;

import java.time.Instant;

public record OrderDispatched(Long orderId, Instant dispatchedDate) {
}
