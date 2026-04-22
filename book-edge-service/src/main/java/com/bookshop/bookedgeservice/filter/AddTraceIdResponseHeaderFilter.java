package com.bookshop.bookedgeservice.filter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AddTraceIdResponseHeaderFilter implements WebFilter, Ordered {

    private static final String TRACE_RESPONSE_HEADER = "traceresponse";
    private final Tracer tracer;

    public AddTraceIdResponseHeaderFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit( () -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add(TRACE_RESPONSE_HEADER, getTraceResponse());
            return Mono.empty();
        });
        return chain.filter(exchange);
    }

    private String getTraceResponse() {
        Span currentSpan = this.tracer.currentSpan();
        assert currentSpan != null;
        var traceId = currentSpan.context().traceId();
        var spanId = currentSpan.context().spanId();
        // Construct W3C compliant traceresponse header
        // Format: 00-{traceId}-{spanId}-01
        var traceResponse = String.format("00-%s-%s-01", traceId, spanId);
        return traceResponse;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
