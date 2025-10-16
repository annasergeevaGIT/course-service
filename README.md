# Course Service

This microservice manages courses for the E-Learning Platform

## Related Services

| Service | Description |
|----------|--------------|
| [Enrollment Service](https://github.com/annasergeevaGIT/enrollment-service) | Handles student enrollments |
| [Review Service](https://github.com/annasergeevaGIT/review-service) | Manages course reviews |
| [Course Aggregate Service](../course-aggregate-service) | Aggregates course and review data |
| [Gateway Service](../gateway-service) | Routes requests to microservices |
| [Config Server](../config-server) | Centralized configuration storage |

## Overview

The **Course Service** provides CRUD operations for course entities and exposes REST endpoints for other microservices (e.g., Enrollment and Review Services).  
It can be implemented using either:
- **Spring WebFlux (Reactive version)**, or
- **Spring Boot with Virtual Threads (Project Loom version)**

This allows direct comparison of scalability and performance between the two concurrency models.

## Functionality

- Create, read, update, delete courses  
- Retrieve all courses with optional filtering  
- Publish course events to Kafka (for asynchronous updates in other services)  
- Store data in PostgreSQL  

## Endpoints

| Method | Endpoint | Description |
|---------|-----------|-------------|
| `GET` | `/courses` | List all courses |
| `GET` | `/courses/{id}` | Get course by ID |
| `POST` | `/courses` | Create a new course |
| `PUT` | `/courses/{id}` | Update an existing course |
| `DELETE` | `/courses/{id}` | Delete a course |

## Tech Stack

- **Java 21**
- **Spring Boot 3**
- **PostgreSQL**
- **Kafka**
- **Micrometer / Prometheus**
- **Eureka Discovery**
- **Spring Cloud Config**

## Build & Run

```bash
docker-compose up -d
./gradlew bootRun
