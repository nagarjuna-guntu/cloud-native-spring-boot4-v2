package com.bookshop.bookdispatchservice.event;

import java.time.Instant;

public record OrderDispatched(Long orderId, Instant dispatchedDate) {
}
