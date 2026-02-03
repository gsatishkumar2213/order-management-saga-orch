package org.example.kafka.paymentservice.kafka;

import org.example.kafka.events.OrderCreatedEvent;
import org.example.kafka.events.PaymentProcessedEvent;
import org.example.kafka.paymentservice.service.PaymentOrchestrationService;
import org.example.kafka.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {
    private PaymentService paymentService;
    private PaymentOrchestrationService paymentOrchestrationService;
    private PaymentEventProducer paymentEventProducer;
    private Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);

    public PaymentEventConsumer(PaymentService paymentService,
                                PaymentOrchestrationService paymentOrchestrationService,
                                PaymentEventProducer paymentEventProducer) {
        this.paymentService = paymentService;
        this.paymentOrchestrationService = paymentOrchestrationService;
        this.paymentEventProducer = paymentEventProducer;
    }


    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void consumeOrderEvent(OrderCreatedEvent orderCreatedEvent) {
        System.out.println("===== LISTENER CALLED =====");
        logger.info("Consumed OrderCreatedEvent: {}", orderCreatedEvent);
        PaymentProcessedEvent paymentProcessedEvent =
                paymentOrchestrationService.makeWebClientCall(orderCreatedEvent);
        paymentEventProducer.publishPaymentProcessed(paymentProcessedEvent);
    }
}
