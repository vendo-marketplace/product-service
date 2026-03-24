# Product Service

Service responsible for managing **products, categories, and attributes** in the Vendo platform.

The service provides APIs for creating and managing product catalog data used by other services such as orders, search, and inventory.

---

# Tech Stack

* Java 17
* Spring Boot
* MongoDB
* Docker
* Kafka
* Eureka
* Zipkin
* MapStruct
* Lombok
* Maven
* JUnit 5
* Mockito

---

# Architecture

The service follows **Hexagonal Architecture (Ports and Adapters)** and **CRQS** pattern.

The core business logic is isolated from external systems such as databases, REST APIs, or messaging systems.

## Layers

**domain**

Contains the core business logic.

* Models
* Types
* Constants
* Exceptions

**application**

Application use cases.

* Application services
* Validation

**port**

Defines interfaces used to communicate with the outside world.

* Input ports (use cases)
* Output ports (repositories)

**adapter**

External integrations.

adapter.in

* REST Controllers
* Request / Response DTO
* Exception handlers

adapter.out

* Database repositories

---

# Project Structure

```
src
 └── main
     └── java
         └── com.vendo.product_service
             ├── adapter
             │   └── product
             │       └── in
             │       └── out
             ├── application
             │   └── product
             ├── domain
             │   └── product
             ├── port
             │   └── product
             └── infrastructure
```

---

# Prerequisites

Before running this service, you need to start required infrastructure services.

## Dependencies

This service depends on:

- **Config Server** – provides externalized configuration
- **Service Registry (Eureka)** – service discovery

---

## 1. Clone and run Config Server

```
git clone https://github.com/vendo-marketplace/config-server
cd config-server
mvn spring-boot:run
```


---

## 2. Clone and run Service Registry

```
git clone https://github.com/vendo-marketplace/registry-service
cd registry-service
mvn spring-boot:run
```


# Running the Service

---

## 3. Run application

Or build and run:

```
mvn clean package
java -jar target/product-service.jar
```

---

# Environment Variables

| Variable          | Description       | Default   |
|-------------------|-------------------|-----------|
| CONFIG_SERVER_URL | Config server url | 8010      |

---

# API Documentation

Swagger UI:

```
http://localhost:8050/swagger-ui.html
```

OpenAPI specification:

```
http://localhost:8050/v3/api-docs
```

---

# Running Tests

Run all tests

```
mvn test
```

Run integration tests

```
mvn verify
```

---

# Code Style

The project follows standard **Java code conventions**.

Key principles:

* Clean Architecture
* SOLID principles
* Immutable DTOs
* Constructor injection
* Clear separation between layers

---

# Contributing

1. Create feature branch
2. Write tests
3. Ensure tests pass
4. Create pull request
