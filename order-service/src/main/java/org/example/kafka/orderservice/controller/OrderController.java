package org.example.kafka.orderservice.controller;

import org.example.kafka.orderservice.entity.Order;
import org.example.kafka.orderservice.entity.OrderItem;
import org.example.kafka.orderservice.requestDTO.OrderRequestDTO;
import org.example.kafka.orderservice.responseDTO.OrderItemResponseDTO;
import org.example.kafka.orderservice.responseDTO.ResponseDTO;
import org.example.kafka.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private OrderService orderService;
    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDTO> createOrder(
            @RequestBody OrderRequestDTO orderRequestDTO,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        logger.info("Order Request: {}", orderRequestDTO);

        if (idempotencyKey != null) {
            Optional<Order> existing = orderService.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                logger.info("Order was already processed for the orderId: {}",
                        existing.get().getOrderId());
                Order existingOrder = existing.get();
                List<OrderItemResponseDTO> items = existingOrder.getOrderItems().stream()
                                                                .map(this::convertToResponseDTO).toList();
                ResponseDTO responseDTO = new ResponseDTO(existingOrder.getOrderId(),
                        existingOrder.getOrderStatus(),
                        existingOrder.getAmount(),
                        existingOrder.getCustomerAddress(),
                        items);
                return ResponseEntity.ok(responseDTO);
            }
        }

        Order orderResponse = orderService.createOrder(orderRequestDTO, idempotencyKey);
        logger.info("Returned order items size: {}", orderResponse.getOrderItems().size());

        List<OrderItemResponseDTO> orderItemResponseDTOList = orderResponse.getOrderItems().stream()
                                                                           .map(this::convertToResponseDTO).toList();

        ResponseDTO responseDTO = new ResponseDTO(orderResponse.getOrderId(),
                orderResponse.getOrderStatus(),
                orderResponse.getAmount(),
                orderResponse.getCustomerAddress(),
                orderItemResponseDTOList);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDTO> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        List<OrderItemResponseDTO> orderItemResponseDTOList =
                order.getOrderItems().stream()
                     .map(this::convertToResponseDTO)
                     .toList();
        ResponseDTO responseDTO = new ResponseDTO(order.getOrderId(), order.getOrderStatus(),
                order.getAmount(),
                order.getCustomerAddress(), orderItemResponseDTOList);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ResponseDTO>> getOrderByCustomerId(@PathVariable String customerId) {
        List<Order> orders = orderService.getOrderByCustomerId(customerId);
        List<ResponseDTO> responseDTOS =
                orders.stream().map(order -> new ResponseDTO(order.getOrderId(),
                        order.getOrderStatus()
                        , order.getAmount(), order.getCustomerAddress(),
                        order.getOrderItems().stream()
                             .map(this::convertToResponseDTO).toList())).toList();

        return ResponseEntity.ok(responseDTOS);
    }

    private OrderItemResponseDTO convertToResponseDTO(OrderItem item) {
        return new OrderItemResponseDTO(item.getItemId(), item.getItemName(), item.getQuantity());
    }
}
