package com.bookshop.bookedgeservice.web;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;


@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler  {

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ProblemDetail> handleClientResponseException(WebClientResponseException ex) {
        var status = ex.getStatusCode();
        var details = ex.getResponseBodyAsString();
        var message = details.isBlank() ? ex.getMessage() : details;
        log.error("Handle ClientResponseException with status code {}, error details: {}", status, message);

        var problemDetails = ProblemDetail.forStatusAndDetail(status, message);
        return Mono.just(problemDetails);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public Mono<ProblemDetail> handleException(CallNotPermittedException ex) {
        var circuitBreakerName = ex.getCausingCircuitBreakerName();
        var message = ex.getMessage();
        var details = message.isBlank() ? circuitBreakerName : message + ": " + circuitBreakerName;
        log.error("Circuit is OPEN: Skipping downstream call.");
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, details);
        return Mono.just(problemDetails);
    }

    @ExceptionHandler(java.util.concurrent.TimeoutException.class)
    public Mono<ProblemDetail> handleTimeoutException(TimeoutException ex) {
        var message = ex.getMessage();
        log.error("Request has been timed out with the details {}" , message);
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.GATEWAY_TIMEOUT, message);
        return Mono.just(problemDetails);
    }
}
