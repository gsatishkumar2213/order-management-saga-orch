package org.example.kafka.orderservice.service;

import org.example.kafka.events.OrderCreatedEvent;
import org.example.kafka.events.PaymentProcessedEvent;
import org.example.kafka.orderservice.constants.OrderStatus;
import org.example.kafka.orderservice.entity.Order;
import org.example.kafka.orderservice.entity.OrderItem;
import org.example.kafka.orderservice.events.*;
import org.example.kafka.orderservice.exception.CustomException;
import org.example.kafka.orderservice.kafka.OrderEventProducer;
import org.example.kafka.orderservice.repository.OrderRepository;
import org.example.kafka.orderservice.requestDTO.OrderRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private OrderEventProducer orderEventProducer;
    private OrderRepository orderRepository;
    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    // Core Operations
    public Order createOrder(OrderRequestDTO orderRequestDTO, String idemkey) {
        // TODO: Save order, publish OrderCreatedEvent
        Order order = new Order(null, idemkey, orderRequestDTO.customerId()
                , orderRequestDTO.amount(), OrderStatus.PENDING, new ArrayList<>(),
                orderRequestDTO.address());
        List<OrderItem> orderItemList =
                orderRequestDTO.items().stream().map(item ->
                        new OrderItem(order.getOrderId(),
                                item.itemName(),
                                item.quantity(), order)).toList();
        order.setOrderItems(orderItemList);
        Order savedOrder = orderRepository.save(order);
        OrderCreatedEvent orderCreatedEvent =
                new OrderCreatedEvent(savedOrder.getOrderId(),
                        savedOrder.getCustomerId(), savedOrder.getAmount());
        orderEventProducer.publishOrderCreated(orderCreatedEvent);
        return savedOrder;
    }

    public Optional<Order> findByIdempotencyKey(String idempotencyKey) {
        return orderRepository.findByIdempotencyKey(idempotencyKey);
    }

    public Order getOrderById(Long orderId) {
        // TODO: Retrieve order
        Order order = orderRepository
                .findOrderByOrderId(orderId)
                .orElseThrow(() -> new CustomException("No Order found"));
        return order;
    }

    public List<Order> getOrderByCustomerId(String orderId) {
        // TODO: Retrieve order
        List<Order> order = orderRepository
                .findOrderByCustomerId(orderId)
                .orElseThrow(() -> new CustomException("No Order found"));
        return order;
    }

    public Order updateOrderStatus(Long orderId, String status) {
        // TODO: Update status
        Order orderRepo =
                orderRepository.findOrderByOrderId(orderId).orElseThrow(() -> new CustomException("No " +
                        "Order Found"));
        Order order = new Order(orderId, orderRepo.getIdempotencyKey(), orderRepo.getCustomerId(),
                orderRepo.getAmount(),
                status, orderRepo.getOrderItems(), orderRepo.getCustomerAddress());
        orderRepository.save(order);
        return order;
    }

    // Saga Response Handlers
    public void handlePaymentSuccess(PaymentProcessedEvent event) {
        // TODO: Payment succeeded, now update inventory
    }

    public void handlePaymentFailure(PaymentFailedEvent event) {
        // TODO: Payment failed, cancel order
    }

    public void handleInventorySuccess(InventoryUpdatedEvent event) {
        // TODO: Inventory updated, now start shipment
    }

    public void handleInventoryFailure(InventoryUpdateFailedEvent event) {
        // TODO: Inventory failed, refund payment
    }

    public void handleShipmentSuccess(ShipmentStartedEvent event) {
        // TODO: Shipment started, order complete
    }

    public void handleShipmentFailure(ShipmentFailedEvent event) {
        // TODO: Shipment failed, restore inventory and refund
    }
}
