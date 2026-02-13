# Course Service

This microservice manages courses for the E-Learning Platform

## Related Services

| Service                                                               | Description                       |
|-----------------------------------------------------------------------|-----------------------------------|
| [Enrollment Service](https://github.com/annasergeevaGIT/enrollment-service-e-learning-platform)   | Manages course enrollments |
| [Course Service](https://github.com/annasergeevaGIT/course-service-e-learning-platform)   | Handles courses and content|
| [Feedback Service](https://github.com/annasergeevaGIT/eedback-service-e-learning-platform) | Manages ratings and feedback |
| [Course Aggregate Service](https://github.com/annasergeevaGIT/aggregate-service-e-learning-platform)| Aggregates course and review data |
| [Gateway Service](https://github.com/annasergeevaGIT/gateway-service-e-learning-platform)| Routing, security, rate limiting |
| [Discovery Service](https://github.com/annasergeevaGIT/discovery-service-e-learning-platform)| Eureka Service registry |
| [Dispatcher Service](https://github.com/annasergeevaGIT/dispatcher-service-e-learning-platform)| Kafka producer/consumer (event streaming) |
| [Docker Deployment](https://github.com/annasergeevaGIT/dispatcher-service-e-learning-platform)| Centralized configuration management |

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
- **Flyway (DB migrations)**
- **Kafka**
- **Micrometer / Prometheus**
- **Eureka Discovery**
- **Spring Cloud Config**

## Build & Run

```bash
./gradlew clean bootBuildImage
docker-compose up -d
./gradlew test
