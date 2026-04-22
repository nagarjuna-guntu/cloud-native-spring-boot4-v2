package com.bookshop.bookorderservice.order.event;

import java.time.Instant;

public record OrderAccepted(Long orderId, Instant acceptedDate) {
}
