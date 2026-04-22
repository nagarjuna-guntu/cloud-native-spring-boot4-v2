package com.bookshop.bookcatalogservice.web;

public record BookResponse(
        String isbn,
        String title,
        String author,
        Double price,
        String publisher
) {
}
