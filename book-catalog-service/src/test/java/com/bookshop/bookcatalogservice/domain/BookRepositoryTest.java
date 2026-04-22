package com.bookshop.bookcatalogservice.domain;

import com.bookshop.bookcatalogservice.config.DataAuditConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Testcontainers
@Import(DataAuditConfig.class)
class BookRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:15.1");

    @Autowired
    BookRepository bookRepository;

    @Autowired
    JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findByIsbn() {
        var isbn = "1491910771";
        var book = Book.of(
                isbn,
                "Head First Java: A Brain-Friendly Guide",
                "Kathy Sierra",
                9.90, Publisher.O_Reilly.getName());
        jdbcAggregateTemplate.insert(book);
        Optional<Book> actual = bookRepository.findByIsbn(isbn);
        assertTrue(actual.isPresent());
        assertEquals(isbn, actual.get().isbn());
    }

    @Test
    void existsByIsbn() {
        var isbn = "1491910771";
        var book = Book.of(
                isbn,
                "Head First Java: A Brain-Friendly Guide",
                "Kathy Sierra",
                9.90, Publisher.O_Reilly.getName());
        jdbcAggregateTemplate.insert(book);
        boolean actual = bookRepository.existsByIsbn(isbn);
        assertTrue(actual);
    }
}