package com.bookshop.bookedgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BookEdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookEdgeServiceApplication.class, args);
	}

}
