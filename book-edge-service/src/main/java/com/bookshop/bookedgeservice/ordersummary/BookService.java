package com.bookshop.bookedgeservice.ordersummary;

import com.bookshop.bookedgeservice.clients.BookServiceClient;
import com.bookshop.bookedgeservice.config.CachedKeys;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Service
@Slf4j
public class BookService {

    private final BookServiceClient bookServiceClient;
    private final ReactiveRedisTemplate<String, Book> redisTemplate;

    public BookService(BookServiceClient bookServiceClient, ReactiveRedisTemplate<String, Book> redisTemplate) {
        this.bookServiceClient = bookServiceClient;
        this.redisTemplate = redisTemplate;
    }

    @TimeLimiter(name = "bookService")
    @Retry(name = "bookService")
    @CircuitBreaker(name = "bookService", fallbackMethod = "getCachedBook")
    public Mono<Book> findBookByIsbn(String isbn) {
        return bookServiceClient.findBookByIsbn(isbn);
    }

    //The Cache Key:: book-catalog::booksByIsbn::
    public Mono<Book> getCachedBook(String isbn, Throwable  ex) {
        var message = Objects.requireNonNullElse(ex.getCause().getMessage(), ex.getMessage());
        log.error("Fallback for the getBook method with the cause {} ", message);
        var cacheKeyBooksByIsbn = CachedKeys.bookByIsbn(isbn);
        return redisTemplate.opsForValue()
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
