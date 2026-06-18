package com.bookshop.bookorderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;

@ImportTestcontainers(TestcontainersConfiguration.class)
@SpringBootTest
class BookOrderServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
