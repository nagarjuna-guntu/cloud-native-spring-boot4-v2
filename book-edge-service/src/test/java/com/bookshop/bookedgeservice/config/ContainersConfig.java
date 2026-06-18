package com.bookshop.bookedgeservice.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;


public class ContainersConfig {

    @Container
    @ServiceConnection(name = "redis")
    public static GenericContainer<?> redisContainer =
        new GenericContainer<>("redis:7").withExposedPorts(6379);


}
