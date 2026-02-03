package org.example.kafka.paymentservice.kafka;

import org.example.kafka.events.PaymentProcessedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {
    private KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentProcessed(PaymentProcessedEvent paymentProcessedEvent) {
        kafkaTemplate.send("payment-completed", paymentProcessedEvent.orderId().toString(),
                paymentProcessedEvent);
    }
}
