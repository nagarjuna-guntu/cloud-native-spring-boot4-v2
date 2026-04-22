package com.bookshop.bookedgeservice.clients;

import com.bookshop.bookedgeservice.config.CachedKeys;
import com.bookshop.bookedgeservice.config.ServiceClientConfigProperties;
import com.bookshop.bookedgeservice.ordersummary.Book;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Component
@Slf4j
public class BookClient {
    private final WebClient webClient;
    private final ServiceClientConfigProperties clientProperties;
    private final ReactiveRedisTemplate<String, Book> booksByIsbnRedisTemplate;

    public BookClient(WebClient.Builder webClienBuilder, ServiceClientConfigProperties clientProperties, ReactiveRedisTemplate<String, Book> booksByIsbnRedisTemplate) {
        this.webClient = webClienBuilder.baseUrl(clientProperties.catalogServiceUrl()).build();
        this.clientProperties = clientProperties;
        this.booksByIsbnRedisTemplate = booksByIsbnRedisTemplate;
    }


    @TimeLimiter(name = "bookService")
    @Retry(name = "bookService")
    @CircuitBreaker(name = "bookService", fallbackMethod = "getCachedBook")
    public Mono<Book> getBook(String isbn) {
        log.info("Fetching book by ISBN {} ", isbn);
        return webClient.get()
                .uri("/books/{ISBN}", isbn)
                .retrieve()
                .bodyToMono(Book.class);
    }

    //The Cache Key:: book-catalog::booksByIsbn::
    public Mono<Book> getCachedBook(String isbn, Throwable  ex) {
        var message = Objects.requireNonNullElse(ex.getCause().getMessage(), ex.getMessage());
        log.error("Fallback for the getBook method with the cause {} ", message);
        var cacheKeyBooksByIsbn = CachedKeys.bookByIsbn(isbn);
        return booksByIsbnRedisTemplate.opsForValue()
                .get(cacheKeyBooksByIsbn)
                .timeout(Duration.ofMillis(800))
                // CASE 1: Cache Miss (Key not found) -> Re-throw original error to Advice
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Cache Miss (Key not found) {} ", cacheKeyBooksByIsbn);
                    return Mono.error(ex);
                }))
                // CASE 2: Cache Failure (Redis down) -> Still re-throw original error to Advice
                .onErrorResume(error -> {
                    log.error("Redis error during fallback: {}", error.getMessage());
                    return Mono.error(ex);
                });

    }
}
