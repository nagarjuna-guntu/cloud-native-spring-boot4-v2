package com.bookshop.bookedgeservice;

import com.bookshop.bookedgeservice.config.ContainersConfig;
import com.bookshop.bookedgeservice.config.RedisTemplateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(RedisTemplateConfig.class)
@ImportTestcontainers(ContainersConfig.class)
@SpringBootTest
class BookEdgeServiceApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@MockitoBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext);
	}

}
