package org.example.kafka.orderservice.repository;

import org.example.kafka.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findOrderByOrderStatus(String orderStatus);

    Optional<List<Order>> findOrderByCustomerId(String customerId);

    Optional<Order> findOrderByOrderId(Long orderId);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

}
