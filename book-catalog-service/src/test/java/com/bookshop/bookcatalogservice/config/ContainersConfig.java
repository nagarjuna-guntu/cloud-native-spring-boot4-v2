package com.bookshop.bookcatalogservice.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;


public class ContainersConfig {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:15.1");
}
