package com.bookshop.bookorderservice.config;

import com.bookshop.bookorderservice.order.domain.OrderService;
import com.bookshop.bookorderservice.order.event.OrderDispatched;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class OrderFunctionsConfig {

    @Bean
    public Consumer<OrderDispatched> orderDispatched(OrderService orderService) {
        return orderDispatched -> {
            orderService.consumeOrderDispatchedEvent(orderDispatched);
            log.info("The order with order id {} is dispatched and dispatched date is {}",
                    orderDispatched.orderId(), orderDispatched.dispatchedDate());
        };

    }
}
