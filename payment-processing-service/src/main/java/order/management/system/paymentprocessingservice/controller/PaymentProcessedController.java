package order.management.system.paymentprocessingservice.controller;

import order.management.system.paymentprocessingservice.entity.Payment;
import order.management.system.paymentprocessingservice.events.OrderCreatedEvent;
import order.management.system.paymentprocessingservice.events.PaymentProcessedEvent;
import order.management.system.paymentprocessingservice.service.PaymentProcessingServic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-process")
public class PaymentProcessedController {
    private PaymentProcessingServic paymentProcessingServic;
    private Logger log = LoggerFactory.getLogger(PaymentProcessedController.class);

    public PaymentProcessedController(PaymentProcessingServic paymentProcessingServic) {
        this.paymentProcessingServic = paymentProcessingServic;
    }

    @PostMapping
    public ResponseEntity<PaymentProcessedEvent> processPayment(@RequestBody OrderCreatedEvent orderCreatedEvent) {
        Payment payment = paymentProcessingServic.checkIfPaymentPresent(orderCreatedEvent);
        PaymentProcessedEvent paymentProcessedEvent =
                new PaymentProcessedEvent(payment.getOrderId(), payment.getId(),
                        payment.getCustomerId(), payment.getStatus(), payment.getAmount());
        return ResponseEntity.status(HttpStatus.OK).body(paymentProcessedEvent);
    }
}
