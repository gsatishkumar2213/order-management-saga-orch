package org.example.kafka.paymentservice.service;

import org.example.kafka.events.OrderCreatedEvent;
import org.example.kafka.events.PaymentProcessedEvent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PaymentOrchestrationService {
    private final WebClient webClient;

    public PaymentOrchestrationService(WebClient webClient) {
        this.webClient = webClient;
    }

    public PaymentProcessedEvent makeWebClientCall(OrderCreatedEvent orderCreatedEvent) {
        return webClient.post()
                        .uri("/payment-process")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(orderCreatedEvent)
                        .retrieve().bodyToMono(PaymentProcessedEvent.class).block();
    }
}
