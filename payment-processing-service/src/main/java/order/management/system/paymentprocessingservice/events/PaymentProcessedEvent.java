package order.management.system.paymentprocessingservice.events;


public record PaymentProcessedEvent(
        Long orderId,
        Long paymentId,
        String customerId,
        String status,
        double amount
) {
}