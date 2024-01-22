package ru.otus.client.configuration;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfiguration {

    private final Resource trustStore;
    private final String trustStorePassword;

    public RestTemplateConfiguration(@Value("${client.ssl.trust-store}") Resource trustStore,
                                     @Value("${client.ssl.trust-store-password}") String trustStorePassword) {
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {
        var sslContext = new SSLContextBuilder()
                .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
                .build();
        var sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}
