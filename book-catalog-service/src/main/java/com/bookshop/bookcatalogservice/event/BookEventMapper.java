package com.bookshop.bookcatalogservice.event;

import com.bookshop.bookcatalogservice.domain.Book;
import com.bookshop.bookcatalogservice.web.BookResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class BookEventMapper {

    public BookResponse mapToBookResponse(BookEvent event) {
        return switch (event) {
            case BookCreatedEvent(_, _, var isbn, var title, var author, var price, var publisher) ->
                    toBookResponse(isbn, title, author, price, publisher);
            case BookUpdatedEvent(_, _, var isbn, var title, var author, var price, var publisher) ->
                    toBookResponse(isbn, title, author, price, publisher);
        };
    }

    private BookResponse toBookResponse(String isbn, String title, String author, Double price, String publisher) {
        return new BookResponse(
                isbn,
                title,
                author,
                price,
                publisher
        );
    }

}
