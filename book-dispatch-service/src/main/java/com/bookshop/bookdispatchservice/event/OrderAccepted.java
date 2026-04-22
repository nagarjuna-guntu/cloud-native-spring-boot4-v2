package com.bookshop.bookdispatchservice.event;

import java.time.Instant;

public record OrderAccepted(Long orderId, Instant acceptedDate) {}

