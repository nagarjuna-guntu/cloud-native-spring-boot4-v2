package com.bookshop.bookedgeservice.config;

import com.bookshop.bookedgeservice.clients.BookServiceClient;
import com.bookshop.bookedgeservice.clients.OrderServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.support.WebClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.HttpServiceGroup;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "order-service", types = OrderServiceClient.class, clientType = HttpServiceGroup.ClientType.WEB_CLIENT)
@ImportHttpServices(group = "catalog-service", types = BookServiceClient.class, clientType = HttpServiceGroup.ClientType.WEB_CLIENT)

public class OrderSummaryClientConfig {

    @Bean
    public WebClientHttpServiceGroupConfigurer httpServiceGroupConfigurer(ReactiveClientRegistrationRepository clientRegistration,
                                                                          ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
                                                                          ServiceClientConfigProperties serviceClientProperties) {

        var oauth2ExchangeFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistration,
                authorizedClientRepository);
        oauth2ExchangeFilter.setDefaultClientRegistrationId("keycloak");

        return groups -> {
            groups.filterByName("order-service")
                    .forEachClient((_, clientBuilder) -> {
                        clientBuilder.baseUrl(serviceClientProperties.orderServiceUrl());
                        clientBuilder.filter(oauth2ExchangeFilter);
                    });
            groups.filterByName("catalog-service")
                    .forEachClient((_, clientBuilder) -> {
                        clientBuilder.baseUrl(serviceClientProperties.catalogServiceUrl());
                        clientBuilder.filter(oauth2ExchangeFilter);
                    });
        };
    }
}
