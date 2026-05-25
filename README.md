# TinyURL Backend

A scalable TinyURL backend system built using:

- Spring Boot
- PostgreSQL
- Redis
- Apache Kafka
- Docker

## Features

- URL shortening
- URL redirection
- Redis caching
- Kafka async analytics
- Distributed rate limiting
- Dockerized Kafka setup
- Event-driven architecture

---

# Architecture

Client
↓
Rate Limiter
↓
Spring Boot API
├── Redis Cache
├── PostgreSQL
└── Kafka Producer
↓
Kafka Consumer
↓
Analytics Processing

---

# Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot | Backend framework |
| PostgreSQL | Persistent storage |
| Redis | Caching + rate limiting |
| Kafka | Async analytics |
| Docker | Containerization |

---

# Setup Instructions

## Clone Repository

```bash
  git clone <repo-url>
  cd tinyurl
```

---

## Start Redis

```bash
  docker run --name redis -p 6379:6379 -d redis
```

---

## Start Kafka

```bash
  docker compose up -d
```

---

## Create PostgreSQL Database

```sql
CREATE DATABASE tinyurl_db;
```

---

## Configure Local Properties

Create:

```text
src/main/resources/application-local.properties
```

Add:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tinyurl_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword

spring.data.redis.host=localhost
spring.data.redis.port=6379

spring.kafka.bootstrap-servers=localhost:9092
```

---

## Run Application

```bash
  mvn spring-boot:run
```

---

# API Endpoints

## Create Short URL

POST `/api/v1/url`

Request:

```json
{
  "originalUrl": "https://google.com"
}
```

---

## Redirect URL

GET `/{shortCode}`

---

# Scalability Features

- Redis cache-aside pattern
- Kafka async event processing
- Distributed rate limiting
- Stateless backend architecture

---

# Future Improvements

- Kubernetes deployment
- Monitoring with Prometheus + Grafana
- Nginx load balancing
- Custom aliases
- URL expiration