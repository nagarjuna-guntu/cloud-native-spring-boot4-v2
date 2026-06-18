package com.bookshop.bookorderservice;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

class TestcontainersConfiguration {

	@Container
	@ServiceConnection
	public static PostgreSQLContainer postgreSQLContainer =
		new PostgreSQLContainer(DockerImageName.parse("postgres:15.1"));

}
