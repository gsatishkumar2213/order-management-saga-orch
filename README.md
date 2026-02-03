# Order Management System - Saga Orchestration

A microservices-based order management platform demonstrating **Saga Orchestration Pattern** with Spring Boot, Kafka, and PostgreSQL.

## Services

- **Order Service**: REST API for order creation and management with idempotency support
- **Payment Service**: Saga orchestrator that coordinates payment processing via REST calls
- **Payment Processing Service**: REST endpoint handling payment processing with idempotent operations
- **Inventory Service**: (Placeholder for future implementation)
- **Shipment Service**: (Placeholder for future implementation)

## Key Features

- **Saga Orchestration Pattern**: Central orchestrator (payment-service) controls the distributed transaction flow
- **Idempotency**: Request-level deduplication using idempotency keys to prevent duplicate orders
- **REST-based Communication**: WebClient for synchronous service-to-service calls
- **Event-driven Architecture**: Kafka for asynchronous communication between order and payment services
- **Duplicate Detection**: Prevents duplicate payments for the same order
- **PostgreSQL Persistence**: Reliable data storage across services
- **Docker Compose with KRaft**: Complete end-to-end deployment with PostgreSQL & Kafka (KRaft mode)

## How It Works

1. **order-service** creates an order → publishes `order-created` event to Kafka
2. **payment-service** consumes `order-created` event → makes REST call to payment-processing-service
3. **payment-processing-service** processes payment → returns PaymentProcessedEvent
4. **payment-service** receives response → publishes `payment-completed` event to Kafka
5. **order-service** consumes `payment-completed` → updates order status to COMPLETED

Payment-service acts as the orchestrator, controlling the flow and handling responses.

## Tech Stack

Spring Boot, Kafka (KRaft mode), PostgreSQL, Docker, Docker Compose, WebClient, Maven

## Running Locally with Docker

```bash
docker-compose up
```

Services will be available at:
- Order Service: http://localhost:9089
- Payment Service: http://localhost:9090
- Payment Processing Service: http://localhost:9091
- PostgreSQL: localhost:5432
- Kafka: localhost:9092

## Database

Three PostgreSQL databases (automatically created via init.sql):
- `order_service_db` - Orders and order items
- `payment_service_db` - Payment tracking and orchestration state
- `payment_processing_db` - Payment processing details

## Idempotency

Request-level idempotency prevents duplicate orders:

```bash
curl -X POST http://localhost:9089/ \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: order-123" \
  -d '{
    "customerId": "test-user",
    "amount": 100.0,
    "items": [{"itemName": "Phone", "quantity": 1}],
    "address": "123 Main St"
  }'
```

Sending the same request with the same `Idempotency-Key` returns the same order (no duplicate created).

## Comparison

See **Saga Choreography Pattern** implementation in separate repo:
- https://github.com/gsatishkumar2213/order-management

## Orchestration vs Choreography

| Aspect | Orchestration | Choreography |
|--------|---------------|--------------|
| Control Flow | Centralized (clear) | Distributed (event-driven) |
| Service Coupling | Tighter (knows all services) | Looser (event-based) |
| Complexity | More complex setup | Simpler setup |
| Debugging | Easier to trace | Harder to follow flow |
| Compensation | Central retry logic | Each service handles own |

## Key Learnings

- **Orchestration Benefits**: Clear control flow, easier compensation logic, centralized retry handling
- **Idempotency**: Critical for handling duplicate requests and ensuring exactly-once semantics
- **WebClient**: Effective for synchronous REST calls within Kafka consumers
- **Docker Networking**: Services communicate using service names (e.g., `payment-processing-service:9091`)
- **KRaft Mode**: Modern Kafka without Zookeeper for simpler deployment

## Architecture

```
┌──────────────────────┐
│   Order Service      │
│  (REST API)          │
└──────────┬───────────┘
           │ publishes order-created
           ▼
       ┌─────────────┐
       │    Kafka    │
       └─────────────┘
           ▲
           │ consumes order-created
           │ publishes payment-completed
           ▼
┌──────────────────────────────┐
│   Payment Service            │
│ (Saga Orchestrator)          │
│ - Consumes events            │
│ - Makes REST calls           │
│ - Publishes responses        │
└──────────────┬───────────────┘
               │ REST call
               ▼
┌──────────────────────────────┐
│ Payment Processing Service   │
│ (REST Endpoint)              │
│ - Processes payments         │
│ - Handles idempotency        │
│ - Persists to DB             │
└──────────────────────────────┘
```

## Testing the Flow

1. Create an order:
```bash
curl -X POST http://localhost:9089/ \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-order-1" \
  -d '{
    "customerId": "test-user",
    "amount": 100.0,
    "items": [{"itemName": "Laptop", "quantity": 1}],
    "address": "123 Main St"
  }'
```

2. Watch the logs:
```bash
docker-compose logs -f order-service
docker-compose logs -f payment-service
docker-compose logs -f payment-processing-service
```

3. Verify data in PostgreSQL:
```bash
docker exec -it order-management-saga-orch-postgres-1 psql -U gondasatishkumar -d order_service_db -c "SELECT * FROM orders;"
```

## Deployment

Docker Compose handles everything:
- Kafka with KRaft mode (no Zookeeper needed)
- PostgreSQL with auto-initialization
- All three microservices
- Health checks and dependencies

Just run: `docker-compose up`

## Author

Satish Kumar - Building production-grade microservices with deep understanding of distributed transaction patterns.