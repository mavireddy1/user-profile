# User Profile Microservice

A Spring Boot microservice for managing user profile details. It exposes REST APIs for creating, retrieving, updating, and deleting user profiles. The service uses an in-memory HashMap as a sample data store (repository layer) and makes downstream calls to an **Identity Service** to keep identity records in sync.

---

## Table of Contents

- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Identity Service Integration](#identity-service-integration)
- [Configuration](#configuration)
- [Running Tests](#running-tests)

---

## Architecture

```
Client → UserProfileController → UserProfileService → UserProfileRepository (HashMap)
                                        ↓
                               IdentityServiceClient → Identity Service (downstream)
```

The service follows a classic layered architecture:
- **Controller** – Handles HTTP requests/responses
- **Service** – Encapsulates business logic
- **Repository** – Data access layer backed by an in-memory `HashMap`
- **Client** – HTTP client for communicating with the Identity Service

---

## Features

- ✅ **Create** a new user profile
- ✅ **Retrieve** a user profile by ID
- ✅ **List** all user profiles
- ✅ **Update** an existing user profile
- ✅ **Delete** a user profile
- ✅ **Input validation** with meaningful error messages
- ✅ **Global exception handling** with structured API responses
- ✅ **Downstream integration** with Identity Service (register / update / delete)
- ✅ **Health & metrics** via Spring Boot Actuator

---

## Tech Stack

| Technology         | Version  |
|--------------------|----------|
| Java               | 17       |
| Spring Boot        | 3.2.3    |
| Spring Web (REST)  | included |
| Spring Validation  | included |
| Spring Actuator    | included |
| Lombok             | included |
| Maven              | 3.x      |
| JUnit 5 + Mockito  | included |

---

## Project Structure

```
user-profile/
├── src/
│   ├── main/
│   │   ├── java/com/example/userprofile/
│   │   │   ├── UserProfileApplication.java        # Entry point
│   │   │   ├── client/
│   │   │   │   └── IdentityServiceClient.java     # Identity Service HTTP client
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java                 # Bean configuration (RestTemplate)
│   │   │   ├── controller/
│   │   │   │   └── UserProfileController.java     # REST endpoints
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java               # Generic API response wrapper
│   │   │   │   ├── IdentityUserRequest.java       # Identity Service request DTO
│   │   │   │   ├── IdentityUserResponse.java      # Identity Service response DTO
│   │   │   │   ├── UserProfileRequest.java        # Create/Update request DTO
│   │   │   │   └── UserProfileResponse.java       # Response DTO
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java    # Centralised error handling
│   │   │   │   ├── IdentityServiceException.java
│   │   │   │   ├── UserProfileAlreadyExistsException.java
│   │   │   │   └── UserProfileNotFoundException.java
│   │   │   ├── model/
│   │   │   │   └── UserProfile.java               # Domain model
│   │   │   ├── repository/
│   │   │   │   ├── UserProfileRepository.java     # Repository interface
│   │   │   │   └── UserProfileRepositoryImpl.java # HashMap-backed implementation
│   │   │   └── service/
│   │   │       ├── UserProfileService.java        # Service interface
│   │   │       └── UserProfileServiceImpl.java    # Service implementation
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/userprofile/
│           ├── UserProfileApplicationTests.java
│           ├── controller/
│           │   └── UserProfileControllerTest.java
│           ├── repository/
│           │   └── UserProfileRepositoryImplTest.java
│           └── service/
│               └── UserProfileServiceImplTest.java
└── pom.xml
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.x

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

The service starts on **port 8080** by default.

---

## API Endpoints

Base URL: `http://localhost:8080/api/users`

### Create User Profile
```
POST /api/users/{userId}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+12345678901",
  "address": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "country": "US",
  "zipCode": "62701",
  "bio": "Software engineer"
}
```
**Response:** `201 Created`

---

### Get User Profile
```
GET /api/users/{userId}
```
**Response:** `200 OK`

---

### Get All User Profiles
```
GET /api/users
```
**Response:** `200 OK`

---

### Update User Profile
```
PUT /api/users/{userId}
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  ...
}
```
**Response:** `200 OK`

---

### Delete User Profile
```
DELETE /api/users/{userId}
```
**Response:** `200 OK`

---

### Response Format

All endpoints return a consistent response envelope:

```json
{
  "success": true,
  "message": "User profile created successfully",
  "data": { ... }
}
```

---

## Identity Service Integration

On every **create**, **update**, or **delete** operation, this service makes a downstream HTTP call to the Identity Service to keep identity records in sync:

| Operation | HTTP Method | Identity Service Endpoint            |
|-----------|-------------|--------------------------------------|
| Create    | POST        | `{identity.service.base-url}/api/identity/users`        |
| Update    | PUT         | `{identity.service.base-url}/api/identity/users/{userId}` |
| Delete    | DELETE      | `{identity.service.base-url}/api/identity/users/{userId}` |

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
server.port=8080

# URL of the downstream Identity Service
identity.service.base-url=http://localhost:8081
```

---

## Running Tests

```bash
mvn test
```

Tests cover:
- **Repository layer**: HashMap CRUD operations
- **Service layer**: Business logic with mocked repository and identity client
- **Controller layer**: REST endpoint behavior via MockMvc

---

## Health Check

```
GET http://localhost:8080/actuator/health
```
