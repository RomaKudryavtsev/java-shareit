package ru.practicum.gateway.client.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

public class BaseClient {
    @Value("${shareit-server.url}")
    protected String baseUrl;
    protected final WebClient webClient = WebClient.create();
}
