package ru.otus.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ClientRunner implements CommandLineRunner  {

    private final RestTemplate restTemplate;
    private final String clientUri;

    public ClientRunner(@Autowired RestTemplate restTemplate, @Value("${client.uri}") String clientUri) {
        this.restTemplate = restTemplate;
        this.clientUri = clientUri;
    }

    @Override
    public void run(String... args) {
        String response = restTemplate.getForObject(clientUri, String.class);
        log.info("Response from the web-server: {}", response);
    }
}
