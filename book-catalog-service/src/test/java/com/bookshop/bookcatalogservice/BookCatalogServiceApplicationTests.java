package com.bookshop.bookcatalogservice;

import com.bookshop.bookcatalogservice.config.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ImportTestcontainers(ContainersConfig.class)
class BookCatalogServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
