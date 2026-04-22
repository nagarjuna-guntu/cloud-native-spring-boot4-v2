package com.bookshop.bookcatalogservice.web;

import com.bookshop.bookcatalogservice.config.SecurityConfig;
import com.bookshop.bookcatalogservice.domain.BookAlreadyExistsException;
import com.bookshop.bookcatalogservice.domain.BookNotFoundException;
import com.bookshop.bookcatalogservice.domain.BookService;
import com.bookshop.bookcatalogservice.domain.Publisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, BookControllerAdvice.class})
class BookControllerTest {

    private static final String ROLE_EMPLOYEE = "ROLE_employee";
    private static final String ROLE_CUSTOMER = "ROLE_customer";

    @Autowired
    MockMvcTester mockMvcTester;

    @MockitoBean
    BookService bookService;

    @Autowired
    JsonMapper jsonMapper;

    @Test
    void whenViewAllAuthenticatedShouldReturnBokResponses() {
        var books = List.of(
                new BookResponse(
                        "1491910771",
                        "Head First Java: A Brain-Friendly Guide",
                        "Kathy Sierra", 9.90,
                        Publisher.O_Reilly.getName()),
                new BookResponse(
                        "0134685997",
                        "Effective Java 3rd Edition",
                        "Joshua Bloch", 59.99,
                        Publisher.Addison_Wesley.getName())
        );

        when(bookService.viewBooks()).thenReturn(books);
        assertThat(mockMvcTester.get().uri("/books")
                .with(jwt()))
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .extractingPath("$")
                .asArray()
                .hasSize(2);
    }

    @Test
    void whenViewAllNotAuthenticatedShouldReturn200() {
        var books = List.of(
                new BookResponse(
                        "1491910771",
                        "Head First Java: A Brain-Friendly Guide",
                        "Kathy Sierra", 9.90,
                        Publisher.O_Reilly.getName()),
                new BookResponse(
                        "0134685997",
                        "Effective Java 3rd Edition",
                        "Joshua Bloch", 59.99,
                        Publisher.Addison_Wesley.getName())
        );

        when(bookService.viewBooks()).thenReturn(books);
        assertThat(mockMvcTester.get().uri("/books"))
                .hasStatusOk();
    }

    @Test
    void whenGetByIsbnAndAuthenticatedShouldReturnBook() {
        var book = new BookResponse(
                "1491910771",
                "Head First Java: A Brain-Friendly Guide",
                "Kathy Sierra",
                9.90, Publisher.O_Reilly.getName()
        );
        when(bookService.viewBookDetails(anyString())).thenReturn(book);
        assertThat(
                mockMvcTester.get().uri("/books/1491910771")
                        .with(jwt()))
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .extractingPath("$")
                .convertTo(BookResponse.class)
                .extracting(BookResponse::title)
                .isEqualTo("Head First Java: A Brain-Friendly Guide");
    }

    @Test
    void whenGetByIsbnNotFoundShouldReturn404() {
        when(bookService.viewBookDetails(anyString())).thenThrow(BookNotFoundException.class);
        assertThat(
                mockMvcTester.get().uri("/books/1491910771")
                        .with(jwt()))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenGetByIsbnAndNotAuthenticatedShouldReturn200() {
        var book = new BookResponse(
                "1491910771",
                "Head First Java: A Brain-Friendly Guide",
                "Kathy Sierra",
                9.90, Publisher.O_Reilly.getName()
        );
        when(bookService.viewBookDetails(anyString())).thenReturn(book);
        assertThat(
                mockMvcTester.get().uri("/books/1491910771"))
                .hasStatusOk();
    }


    @Test
    void addBookWithEmployeeRoleShouldCreateBook() {
        var createBookRequest = new CreateBookRequest(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                59.99, Publisher.Addison_Wesley.getName()
        );

        var bookResponse = new BookResponse(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                59.99, Publisher.Addison_Wesley.getName()
        );

        when(bookService.addBook(any(CreateBookRequest.class))).thenReturn(bookResponse);
        assertThat(
                mockMvcTester.post().uri("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createBookRequest))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE)))
                        .with(csrf()))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.author")
                .isEqualTo("Joshua Bloch");
    }

    @Test
    void addAlreadyExistsBookWithEmployeeRoleShouldReturn422() {
        var createBookRequest = new CreateBookRequest(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                59.99, Publisher.Addison_Wesley.getName()
        );

        when(bookService.addBook(any(CreateBookRequest.class))).thenThrow(BookAlreadyExistsException.class);
        assertThat(
                mockMvcTester.post().uri("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createBookRequest))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE)))
                        .with(csrf()))
                .hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void addBookWithCustomerRoleShouldReturn403() {
        var createBookRequest = new CreateBookRequest(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                59.99, Publisher.Addison_Wesley.getName()
        );

        var bookResponse = new BookResponse(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                59.99, Publisher.Addison_Wesley.getName()
        );

        when(bookService.addBook(any(CreateBookRequest.class))).thenReturn(bookResponse);
        assertThat(
                mockMvcTester.post().uri("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createBookRequest))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER)))
                        .with(csrf()))
                .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    void addBookWithUnAuthenticatedShouldReturn401() {
        var createBookRequest = new CreateBookRequest(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                59.99, Publisher.Addison_Wesley.getName()
        );

        var bookResponse = new BookResponse(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                59.99, Publisher.Addison_Wesley.getName()
        );

        when(bookService.addBook(any(CreateBookRequest.class))).thenReturn(bookResponse);
        assertThat(
                mockMvcTester.post().uri("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createBookRequest))
                        .with(csrf()))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void whenUpdateBookWithEmployeeRoleShouldReturn200() {
        var updateBookRequest = """
                {
                  "isbn": "1491910771",
                  "title": "Effective Java 3rd Edition",
                  "author": "Joshua Bloch",
                  "price": 69.99,
                  "publisher": "O'Reilly Media"
                }
                """;
        var bookResponse = new BookResponse(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                69.99, Publisher.O_Reilly.getName()
        );
        when(bookService.editBook(anyString(), any(UpdateBookRequest.class))).thenReturn(bookResponse);
        assertThat(
                mockMvcTester.put().uri("/books/1491910771")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBookRequest)
                        .with(csrf())
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.price")
                .isEqualTo(69.99);

    }

    @Test
    void whenUpdateBookWithInvalidISBNPathShouldReturn400() {
        var updateBookRequest = """
                {
                  "title": "Effective Java 3rd Edition",
                  "author": "Joshua Bloch",
                  "price": 69.99,
                  "publisher": "O'Reilly Media"
                }
                """;

        assertThat(
                mockMvcTester.put().uri("/books/1491910771AC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBookRequest)
                        .with(csrf())
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
                .hasStatus(HttpStatus.BAD_REQUEST);

    }

    @Test
    void whenUpdateBookWithInvalidRequestShouldReturn400() {
        var updateBookRequest = """
                {
                  "title": "Effective Java 3rd Edition",
                  "author": "Joshua Bloch",
                  "price": 0,
                  "publisher": "O'Reilly Media"
                }
                """;

        assertThat(
                mockMvcTester.put().uri("/books/1491910771")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBookRequest)
                        .with(csrf())
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
        .extractingPath("$.detail").isEqualTo("Validation Errors");

    }


    @Test
    void patchBook() {
        var patchBookRequest = """
                {
                  "price": 79.99
                }
                """;
        var bookResponse = new BookResponse(
                "0134685997",
                "Effective Java 3rd Edition",
                "Joshua Bloch",
                79.99, Publisher.O_Reilly.getName()
        );

        when(bookService.editBookPartial(anyString(), anyMap())).thenReturn(bookResponse);
        assertThat(
                mockMvcTester.patch().uri("/books/1491910771")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBookRequest)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE)))
                        .with(csrf()))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.price")
                .isEqualTo(79.99);
    }
}