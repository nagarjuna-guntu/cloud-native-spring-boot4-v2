package com.bookshop.bookedgeservice.clients;

import com.bookshop.bookedgeservice.ordersummary.Book;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("/books")
public interface BookServiceClient {

    @GetExchange("{ISBN}")
    @CircuitBreaker(name = "bookService")
    Mono<Book> findBookByIsbn(@PathVariable String ISBN);
}
