# TinyURL Backend Platform

A production-grade scalable URL shortening platform built using Spring Boot, PostgreSQL, Redis, Kafka, Docker, Nginx, JWT Authentication, Role-Based Access Control (RBAC), Email-Based User Onboarding, and Distributed Rate Limiting.

The platform supports URL shortening, URL redirection, distributed caching, asynchronous analytics processing, load balancing, secure authentication, user onboarding workflows, quota management, and multi-tenant architecture.

---

# Architecture Overview

```text
                        ┌───────────────┐
                        │    Client     │
                        └───────┬───────┘
                                │
                                ▼
                      ┌─────────────────┐
                      │      NGINX      │
                      │ Load Balancer   │
                      └───────┬─────────┘
                              │
                 ┌────────────┴────────────┐
                 │                         │
                 ▼                         ▼
        ┌────────────────┐       ┌────────────────┐
        │     APP-1      │       │     APP-2      │
        │ Spring Boot    │       │ Spring Boot    │
        └───────┬────────┘       └───────┬────────┘
                │                        │
                └──────────┬─────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
  PostgreSQL            Redis             Kafka
```

---

# Features

## URL Management

- Create Short URLs
- Redirect Short URLs
- Delete URLs
- List URLs
- URL Ownership
- URL Quota Enforcement

## Authentication & Authorization

- JWT Authentication
- Spring Security
- Role-Based Access Control (ADMIN / USER)
- BCrypt Password Hashing
- Protected APIs

## User Management

- Create Admin
- Create Users
- User Onboarding Workflow
- Temporary Password Generation
- Email-Based User Onboarding
- Change Password
- Password Expiry Handling
- User Deactivation
- Quota Management
- Current User Profile API

## Distributed System Features

- Redis Caching
- Kafka Event Processing
- Nginx Load Balancing
- Multiple Spring Boot Instances
- Dockerized Infrastructure

## Platform Features

- Swagger/OpenAPI Documentation
- Global Exception Handling
- Distributed Rate Limiting
- Email Notifications
- Multi-Tenant Architecture

---

# Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate

## Database

- PostgreSQL

## Distributed Systems

- Redis
- Apache Kafka
- Nginx

## Security

- JWT
- BCrypt Password Hashing

## Documentation

- Swagger / OpenAPI

## Infrastructure

- Docker
- Docker Compose

## Email

- Spring Mail
- Gmail SMTP

---

# User Roles

## ADMIN

Admin can:

- Create Users
- View All Users
- Update User Quotas
- Set User Quota To Zero
- Deactivate Users
- View All URLs
- Delete Any URL
- Resend Password Setup Links

---

## USER

User can:

- Create URLs
- View Own URLs
- Delete Own URLs
- Change Password
- View Own Profile

---

# Authentication Flow

```text
Admin Creates User
        │
        ▼
Temporary Password Generated
        │
        ▼
Email Sent To User
        │
        ▼
User Login
        │
        ▼
Change Password
        │
        ▼
JWT Issued
        │
        ▼
Protected APIs Accessible
```

---

# API Overview

## Authentication APIs

| Method | Endpoint |
|----------|----------|
| POST | /auth/create-admin |
| POST | /auth/login |
| GET | /auth/me |
| POST | /auth/change-password |
| POST | /auth/resend-password-link |

---

## Admin APIs

| Method | Endpoint |
|----------|----------|
| POST | /admin/users |
| GET | /admin/users |
| PUT | /admin/users/{id}/quota |
| PUT | /admin/users/{id}/quota-zero |
| PUT | /admin/users/{id}/deactivate |

---

## URL APIs

| Method | Endpoint |
|----------|----------|
| POST | /api/v1/url |
| GET | /api/v1/url |
| DELETE | /api/v1/url/{id} |
| GET | /{shortCode} |

---

# Local Development Setup

## Prerequisites

Install:

- Java 21
- Maven
- PostgreSQL
- Redis
- Kafka
- Docker Desktop (Optional)

---

## Database Setup

Create PostgreSQL database:

```sql
CREATE DATABASE tinyurl_db;
```

---

## Configuration

Create:

```text
src/main/resources/application-local.properties
```

Add the following configuration:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/tinyurl_db
spring.datasource.username=<your-db-username>
spring.datasource.password=<your-db-password>

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# JWT
jwt.secret=<your-jwt-secret>

# Gmail SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587

spring.mail.username=<your-email>
spring.mail.password=<your-gmail-app-password>

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## Start Redis

```bash
  docker run -d --name redis -p 6379:6379 redis
```

---

## Start Kafka

```bash
  docker compose up kafka -d
```

---

## Build Project

```bash
  ./mvnw clean package
```

Windows:

```bash
  mvnw.cmd clean package
```

---

## Run Project

```bash
  ./mvnw spring-boot:run
```

Windows:

```bash
  mvnw.cmd spring-boot:run
```

---

## Access Application

Application:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI Docs:

```text
http://localhost:8080/v3/api-docs
```

---

# Running with Load Balancer

This project supports horizontal scaling using:

- Nginx Load Balancer
- Multiple Spring Boot Instances
- PostgreSQL
- Redis
- Kafka

---

## Build Application

```bash
  ./mvnw clean package
```

Verify that the jar exists:

```text
target/tinyurl-0.0.1-SNAPSHOT.jar
```

---

## Start Complete Stack

```bash
  docker compose up --build
```

This starts:

- app1
- app2
- nginx
- postgres
- redis
- kafka

---

## Access Application

Load Balanced URL:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Distributed Rate Limiting

Implemented using:

- Redis
- Spring Filter
- API-specific limits

Example limits:

| API | Limit |
|------|---------|
| Login | 5/min |
| Create User | 2/min |
| Password Reset | 3/min |
| Create URL | 50/min |
| Redirect URL | 1000/min |

---

# Security Features

- JWT Authentication
- Role-Based Access Control
- BCrypt Password Hashing
- User Ownership Validation
- Password Expiry Enforcement
- Global Exception Handling
- Distributed Rate Limiting

---

# Scalability Features

- Stateless Services
- Redis Distributed Cache
- Kafka Event Processing
- Nginx Load Balancing
- Horizontal Scaling
- Dockerized Deployment

---

# Future Enhancements

- AI Admin Agent
- Refresh Token Support
- Audit Logging
- Prometheus Metrics
- Grafana Dashboards
- Kubernetes Deployment
- Subscription Plans
- Usage Analytics

---

# Important Notes

- Never commit credentials or secrets to GitHub.
- `application-local.properties` should be excluded using `.gitignore`.
- Gmail SMTP requires an App Password, not your Gmail account password.
- The application is designed to run behind an Nginx load balancer for horizontal scalability.

---

# Author

**Atharva Dekhane**

Backend Engineering | Distributed Systems | Spring Boot | AI Systems | Scalable Platform Design