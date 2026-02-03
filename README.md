# Order Management System - Saga Orchestration

A microservices-based order management platform demonstrating **Saga Orchestration Pattern** with
Spring Boot, Kafka, and PostgreSQL.

## Services

- **Order Service**: REST API for order creation and management with idempotency support
- **Payment Service**: Saga orchestrator that coordinates payment processing via REST calls
- **Payment Processing Service**: REST endpoint handling payment processing with idempotent
  operations
- **Inventory Service**: (Placeholder for future implementation)
- **Shipment Service**: (Placeholder for future implementation)

## Key Features

- **Saga Orchestration Pattern**: payment-service orchestrates the distributed transaction flow
- **Idempotency**: Request-level idempotency using idempotency keys to prevent duplicate orders
- **REST-based Service Communication**: Payment service uses WebClient for synchronous REST calls to
  payment-processing service
- **Event-driven Architecture**: Kafka for asynchronous communication between order and payment
  services
- **Duplicate Detection**: Prevents duplicate payments for the same order
- **Comprehensive Error Handling**: Validation and exception handling across all services
- **PostgreSQL Persistence**: Reliable data storage across services

## Tech Stack

Spring Boot, Kafka, PostgreSQL, WebClient, Docker, Docker Compose

## Related Repository

See [order-management](https://github.com/gondasatishkumar/order-management) for **Choreography
Pattern** implementation