package com.bookshop.bookorderservice.book;

public sealed interface ApiResponse<T> permits Success, Failure {
    T getData();
    Throwable getError();
}
