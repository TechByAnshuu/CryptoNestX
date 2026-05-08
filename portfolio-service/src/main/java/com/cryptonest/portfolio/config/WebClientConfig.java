package com.cryptonest.portfolio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${coinmarketcap.base-url:https://pro-api.coinmarketcap.com}")
    private String cmcBaseUrl;

    @Value("${coinmarketcap.api-key:}")
    private String cmcApiKey;

    @Bean
    public WebClient webClient() {
        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create()
                .resolver(io.netty.resolver.DefaultAddressResolverGroup.INSTANCE);

        return WebClient.builder()
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                .baseUrl(cmcBaseUrl)
                .defaultHeader("X-CMC_PRO_API_KEY", cmcApiKey)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
