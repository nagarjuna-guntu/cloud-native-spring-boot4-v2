package com.bookshop.bookedgeservice.ordersummary;


public record Book(String isbn, String title,
                   String author, double price, String publisher) {
}
