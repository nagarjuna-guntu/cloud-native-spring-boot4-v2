package com.bookshop.bookorderservice.book;

import com.bookshop.bookorderservice.config.BookClientConfig;
import com.bookshop.bookorderservice.config.ServiceClientProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@Import(BookClientConfig.class)
@TestPropertySource(properties ={
        "book.catalog-service-uri=http://localhost:8001",
        "book.catalog-root-api=/books/"
})
@RestClientTest(BookClient.class)
class BookClientTest {

    @Autowired
    private BookClient bookClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private ServiceClientProperties bookClientProperties;

    @Autowired
    private JsonMapper jsonMapper;

    record BookRecord(Long id, String isbn, String title, String author, String publisher, double price, Instant createdDate, Instant lastModifiedDate, int version) {}


    @Test
    void findBookByIsbn() {
        var bookRecord = new BookRecord(1L, "1234", "ABC", "Nagarjuna", "ABC_Publications", 123, Instant.now(), Instant.now(), 1);
        String fullUri = bookClientProperties.catalogServiceUri().toString() + bookClientProperties.catalogRootApi();
        mockRestServiceServer.expect(requestTo(fullUri + bookRecord.isbn()))
                .andRespond(withSuccess(jsonMapper.writeValueAsString(bookRecord), MediaType.APPLICATION_JSON));
        var result = bookClient.findBookByIsbn("1234");
        assertEquals(bookRecord.isbn, result.isbn());
    }
}