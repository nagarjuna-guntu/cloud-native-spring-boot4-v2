package com.bookshop.bookorderservice.book;

import com.bookshop.bookorderservice.config.BookClientConfig;
import com.bookshop.bookorderservice.config.ServiceClientProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@Import(BookClientConfig.class)
@EnableConfigurationProperties(ServiceClientProperties.class)
@TestPropertySource(properties ={
        "services.catalog.base-url=http://localhost:9011"
})
@RestClientTest(BookClient.class)
class BookClientTest {

    @Autowired
    private BookClient bookClient;

    @Autowired
    private ServiceClientProperties bookClientProperties;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    record BookRecord(Long id, String isbn, String title, String author, String publisher, double price, Instant createdDate, Instant lastModifiedDate, int version) {}


    @AfterEach
    void tearDown() {
        // Clear Spring Security 7 context stores cleanly
        SecurityContextHolder.clearContext();
        TestSecurityContextHolder.clearContext();
    }


    @Test
    void findBookByIsbn() {

        // 1. Build a Spring Security 7 valid JWT Token representation
        Jwt jwt = Jwt.withTokenValue("sb4-ss7-token-example")
                .header("alg", "none")
                .claim("sub", "Nagarjuna")
                .build();

        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Collections.emptyList());

        // 2. Bind the authentication mapping across thread stores securely
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        TestSecurityContextHolder.setContext(context);

        var bookRecord = new BookRecord(1L, "1234", "ABC", "Nagarjuna", "ABC_Publications", 123, Instant.now(), Instant.now(), 1);
        String fullUri = bookClientProperties.catalog().baseUrl();
        mockRestServiceServer.expect(requestTo(fullUri + "/books/" + bookRecord.isbn()))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer sb4-ss7-token-example"))
                .andRespond(withSuccess(jsonMapper.writeValueAsString(bookRecord), MediaType.APPLICATION_JSON));
        var result = bookClient.findBookByIsbn("1234");
        assertEquals(bookRecord.isbn, result.isbn());
    }
}