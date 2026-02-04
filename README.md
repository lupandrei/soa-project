# Restaurant Booking & Real-Time Analytics System

A distributed, event-driven microservices architecture designed for restaurant reservations and live analytical tracking. The system features a redundant gateway cluster, serverless cost estimation (FaaS), and a dual-messaging backbone (Kafka + RabbitMQ) for real-time WebSocket notifications and in-memory analytics history.

## Architecture Overview

This system follows a Service-Oriented Architecture (SOA), splitting functionality into independently deployable microservices communicating through REST APIs, message brokers (RabbitMQ), and event streams (Kafka). The architecture is designed for high availability and low latency, using an Nginx load balancer as the primary ingress point.

## Microservices

### Api Gateway Service (Spring Cloud Gateway)
* **API Gateway Cluster:** Two redundant instances handling centralized routing and security.
* **Load Balancing:** Receives traffic from Nginx and distributes it to internal services.
* **Security:** Manages JWT propagation across the microservice mesh.

### User Service (Spring Boot)
* **Identity Provider:** Two active instances handling user registration and login.
* **Persistence:** Manages user credentials and profiles in a dedicated PostgreSQL (UserDB).
* **Authentication:** Generates and validates JWT tokens for secure access to the platform.

### Restaurant service (Spring Boot)
* **Restaurant Management:** Manages the source of truth for restaurant data.
* **Persistence:** Uses a dedicated PostgreSQL (RestaurantDB) for business data isolation.

### Booking service (Spring Boot)
* **Reservation Logic:** Handles the core business workflow for table bookings.
* **Event Dispatcher:** Upon successful booking, it publishes events to two separate streams:
    * **RabbitMQ:** Sends a `booking.confirmed` event for immediate notifications.
    * **Kafka:** Sends a `booking.analytics` event for historical data tracking.
* **Non-Blocking Flow:** Responds with HTTP 202 Accepted to ensure the UI remains responsive during event propagation.

### Notifications service (Spring Boot)
* **Dual Consumer:**
    * **RabbitMQ Consumer:** Listens for confirmation events to trigger immediate WebSocket pushes.
    * **Kafka Consumer:** Listens for analytics events to populate a volatile in-memory store.
* **In-Memory Analytics:** Stores user history in a `ConcurrentHashMap` (Materialized View), allowing instant retrieval via `/api/analytics`.
* **Real-time Alerts:** WebSocket server pushes booking confirmations directly to the Angular UI.

### Estimator function (Python FaaS)
* **Serverless Logic:** Python-based function that calculates dynamic booking cost estimates.
* **Stateless Execution:** Operates without a database, processing group size and restaurant parameters in real-time.
* **Integration:** Invoked synchronously by the API Gateway during the reservation process.

## Frontend Application

### Host Dashboard (Angular 16)
* **Main Portal:** Single Page Application (SPA) serving as the user entry point.
* **Real-time Updates:** Integrated with WebSockets (STOMP/SockJS) for live booking alerts.
* **Dynamic Content:** Consumes REST APIs for restaurant browsing and FaaS for price estimations.
* **Analytics View:** Displays user-specific booking trends fetched from the Notification Service's memory store.

## Infrastructure & Messaging

* **PostgreSQL:** Two isolated relational databases ensuring the Database-per-Service pattern.
* **RabbitMQ:** Message broker for low-latency, asynchronous notification delivery (decouples Booking from Notifications).
* **Apache Kafka:** Event streaming platform for analytics log retention and history reconstruction.
* **Nginx:** Layer 7 Reverse Proxy and Load Balancer targeting the Gateway cluster on port 8082.
* **Docker Compose:** Orchestrates all containers, including services, brokers, databases, and proxy.

## Key Features

* **High Availability:** Redundant Gateways and Identity services ensure zero-downtime during node failure.
* **Event-Driven Bifurcation:** Separate messaging paths for notifications (RabbitMQ) and analytics (Kafka) to optimize throughput.
* **In-Memory State Management:** Lightning-fast analytics retrieval by bypassing traditional disk-based queries.
* **Transaction Safety:** Asynchronous confirmation flow ensures the core booking process is never blocked by notification delays.

## Project Requirements Checklist

| Requirement | Implementation Details                                         | Status |
| :--- |:---------------------------------------------------------------| :--- |
| Microservices (> 2) | 5+ Services: Gateway, User, Restaurant, Booking, Notifications | ✅ Done |
| Service Redundancy | 2x Gateway instances and 2x Identity Service instances         | ✅ Done |
| Load Balancing | Nginx upstream balancing to the Gateway cluster                | ✅ Done |
| Message Broker | RabbitMQ for async confirmation alerts                         | ✅ Done |
| Event Streaming | Kafka for analytics tracking and historical data logs          | ✅ Done |
| Use a FaaS | Python cost estimation function reacting to REST calls         | ✅ Done |
| Web Application | Angular dashboard with WebSocket push and REST consumption     | ✅ Done |
| Containers | Docker Compose orchestrating the full multi-tier stack         | ✅ Done |
| Documentation | C4 Context/Container diagrams and detailed README              | ✅ Done |

## Getting Started

### Prerequisites
* Docker & Docker Compose
* Java 17+ (for local development)
* Node.js & Angular CLI (for local frontend development)

### Build & Run

```bash
# Start all services (infrastructure + backends + frontends)
docker-compose up -d --build

# View logs
docker-compose logs -f

# Access the application
# Frontend Dashboard: http://localhost:4200
# API Gateway (via Nginx): http://localhost:8082
# RabbitMQ Management: http://localhost:15672