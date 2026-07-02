# PayFlow

> **Production-ready Payment Processing Service** built with Spring Boot
> featuring Razorpay integration, RabbitMQ, Resilience4j, Prometheus,
> Grafana and k6.

## Features

-   Merchant Management
-   API Key Authentication
-   Idempotent Payment Creation
-   Razorpay Order Integration
-   Webhook Processing
-   Retry API
-   RabbitMQ + DLQ
-   Audit Logging
-   Retry, Circuit Breaker & Bulkhead
-   Prometheus + Grafana
-   JUnit 5 + Mockito
-   k6 Performance Testing

## Tech Stack

Java 21 • Spring Boot • PostgreSQL • Redis • RabbitMQ • Razorpay •
Resilience4j • Micrometer • Prometheus • Grafana • Docker • JUnit •
Mockito • k6

## Project Structure

``` text
common/
merchant/
payment/
  audit/
  event/
  gateway/
  metrics/
  notification/
  publisher/
  statemachine/
  webhook/
```

## Architecture

``` text
Merchant
   |
API Key Auth
   |
Payment API
   |
Idempotency
   |
Razorpay
   |
PostgreSQL
   |
RabbitMQ
   |
Notification Consumer

Webhook
   |
State Machine
   |
Audit + Metrics
```

## Grafana

Add your screenshots under `docs/images`.

Dashboard metrics observed:

-   Payments Created: 1149
-   Payment Creation Failed: 46
-   Payments Captured: 44
-   Payments Failed: 2
-   Average Payment Time: 1.51 s
-   Gateway Latency: 0.182 ms
-   CPU Usage: 0.305%
-   Infrastructure Usage: 3.90%
-   Failure Rate: 13.5%

## Unit Testing

-   Merchant: 9 tests
-   Payment: 14 tests
-   Total: 23 tests

All tests passed.

## Payment Load Test

Scenario: - 20 VUs - 30 seconds

Results: - Requests: 195 - Success: 170 - Failed: 25 - Failure Rate:
12.82% - Average Latency: 2.21 s - P95: 3.82 s

Failures were caused by Razorpay API rate limiting
(`BAD_REQUEST_ERROR: Too many requests`), validating Retry and Circuit
Breaker behaviour.

## Webhook Load Test

Scenario: - 20 VUs - 30 seconds

Results: - Requests: 40,854 - Throughput: 1361 req/sec - Success Rate:
100% - Average Latency: 14.55 ms - P95: 47.79 ms

## Reliability

-   Retry
-   Circuit Breaker
-   Bulkhead
-   DLQ
-   API Key Authentication
-   State Machine
-   Audit Logging

## Local Run

``` bash
docker compose up -d
./mvnw spring-boot:run
```

Swagger: `http://localhost:8080/swagger-ui/index.html`

## Resume Highlights

-   Built a production-ready payment service.
-   Integrated Razorpay orders and webhooks.
-   Implemented asynchronous messaging with RabbitMQ.
-   Added Prometheus and Grafana observability.
-   Achieved 1361 webhook requests/sec.
-   Wrote 23 unit tests.
