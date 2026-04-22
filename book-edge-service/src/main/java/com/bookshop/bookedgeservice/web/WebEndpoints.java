package com.bookshop.bookedgeservice.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.ServerRequest.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import javax.xml.catalog.Catalog;

@Configuration
public class WebEndpoints {

    @Bean
    public RouterFunction<ServerResponse> catalogFallbackRoute(CatalogHandler catalogHandler) {
        return RouterFunctions.route()
                .GET("/catalog-fallback/{*ISBN}", catalogHandler::getFallback)
                .POST("/catalog-fallback", catalogHandler::postFallback)
                .build();
    }
}
