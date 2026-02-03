package order.management.system.paymentprocessingservice.service;


import order.management.system.paymentprocessingservice.entity.Payment;
import order.management.system.paymentprocessingservice.events.OrderCreatedEvent;
import order.management.system.paymentprocessingservice.exception.InvalidinputException;
import order.management.system.paymentprocessingservice.repo.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentProcessingServic {
    private Logger log = LoggerFactory.getLogger(PaymentProcessingServic.class);
    private PaymentRepository paymentRepository;

    public PaymentProcessingServic(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment checkIfPaymentPresent(OrderCreatedEvent orderCreatedEvent) {
        Optional<Payment> existing = paymentRepository.findByOrderId(orderCreatedEvent.orderId());
        if (existing.isPresent()) {
            log.info("Payment already processed for orderId: {}", orderCreatedEvent.orderId());
            return existing.get();
        }
        Payment payment = new Payment(null, orderCreatedEvent.orderId(),
                orderCreatedEvent.customerId(),
                orderCreatedEvent.amount(), "PENDING",
                LocalDateTime.now());
        log.info("Payment request has been recieved: " + payment.getOrderId());
        try {
            if (payment == null) {
                throw new InvalidinputException("Payment can not be null");
            }

            if (payment.getOrderId() == null
                    || payment.getAmount() <= 0
                    || (payment.getCustomerId() == null || payment.getCustomerId().isBlank())) {
                throw new InvalidinputException("Invalid Request found: " + payment.toString());
            }

            payment.setStatus("COMPLETED");
            return paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Payment processing failed: " + e.getMessage());
            payment.setStatus("FAILED");
            return paymentRepository.save(payment);
        }
    }
}
