package com.bookshop.bookorderservice.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder(toBuilder = true)
public record Book(
        String isbn,
        String title,
        String author,
        double price,
        String publisher) {
}
