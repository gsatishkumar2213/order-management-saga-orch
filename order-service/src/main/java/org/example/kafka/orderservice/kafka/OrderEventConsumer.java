package org.example.kafka.orderservice.kafka;

import org.example.kafka.events.PaymentProcessedEvent;
import org.example.kafka.orderservice.entity.Order;
import org.example.kafka.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private OrderService orderService;
    private Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    public OrderEventConsumer(OrderService orderService) {
        logger.info("===== OrderEventConsumer CREATED =====");
        this.orderService = orderService;
    }

    @KafkaListener(topics = "payment-completed", groupId = "order-service-group")
    public void consumerPaymentProcessEvent(PaymentProcessedEvent paymentProcessedEvent) {
        logger.info("===== CONSUMED PaymentProcessedEvent =====" + paymentProcessedEvent);
        Order response = orderService.updateOrderStatus(paymentProcessedEvent.orderId(),
                paymentProcessedEvent.status());
        logger.info("Order Process Completed: " + response.toString());

    }
}
