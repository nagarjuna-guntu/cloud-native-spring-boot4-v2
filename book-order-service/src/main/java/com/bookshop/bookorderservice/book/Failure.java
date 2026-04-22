package com.bookshop.bookorderservice.book;

public record Failure<T>(String message, Throwable throwable) implements ApiResponse<T> {

    @Override
    public T getData() {
        throw new RuntimeException(message());
    }

    @Override
    public Throwable getError() {
        return throwable;
    }
}
