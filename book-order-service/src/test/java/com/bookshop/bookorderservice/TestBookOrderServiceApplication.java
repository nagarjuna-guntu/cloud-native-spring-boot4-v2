package com.bookshop.bookorderservice;

import org.springframework.boot.SpringApplication;

public class TestBookOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(BookOrderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
