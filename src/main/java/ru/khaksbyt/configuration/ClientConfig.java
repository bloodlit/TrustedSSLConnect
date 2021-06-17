package ru.khaksbyt.configuration;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public HttpClientConnectionManager connectionManager() {
        return PoolingHttpClientConnectionManagerBuilder.create()

                .build();
    }



}
