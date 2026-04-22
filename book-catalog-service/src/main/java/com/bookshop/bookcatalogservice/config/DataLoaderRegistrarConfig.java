package com.bookshop.bookcatalogservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DataLoaderBeanRegistrar.class)
public class DataLoaderRegistrarConfig {
}
