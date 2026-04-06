# Finance Microservices — Zorvyn FinTech

Production-grade microservices backend for the Zorvyn Finance Dashboard.

## Architecture

```
finance-microservices/
├── common/
│   ├── common-models       → Shared enums, ApiResponse, ErrorResponse
│   ├── common-exceptions   → Custom exceptions + GlobalExceptionHandler
│   ├── common-utils        → PageRequestUtil, DateUtil, AppConstants
│   └── common-security     → JwtTokenProvider, JwtAuthenticationFilter
└── services/
    ├── service-discovery   → Eureka Server          :8761
    ├── config-server       → Spring Cloud Config    :8888
    ├── api-gateway         → Spring Cloud Gateway   :8080
    ├── auth-service        → JWT Auth               :8081
    ├── user-service        → User Management        :8082
    ├── record-service      → Financial Records      :8083
    └── dashboard-service   → Analytics              :8084
```

## Design Principles

- Each service owns its own MySQL database schema
- Common functionality is packaged as shared dependency jars
- JWT validation happens at the gateway level — downstream services trust the forwarded headers
- All APIs return a standard `ApiResponse<T>` envelope
- Soft delete pattern for financial records
- Role-based access: VIEWER (read), ANALYST (read + write), ADMIN (full)

## Prerequisites

- Java 17
- Maven 3.8+
- MySQL 8.0+

## Database Setup

```sql
CREATE DATABASE auth_service;
CREATE DATABASE user_service;
CREATE DATABASE record_service;
```

> dashboard-service reads from `record_service` (shared read, `ddl-auto=none`)

## Running the Services

Start in this exact order:

```bash
# 1. Build everything
mvn clean install -DskipTests

# 2. Service Discovery
cd services/service-discovery && mvn spring-boot:run

# 3. Config Server
cd services/config-server && mvn spring-boot:run

# 4. API Gateway
cd services/api-gateway && mvn spring-boot:run

# 5. Auth Service
cd services/auth-service && mvn spring-boot:run

# 6. User Service
cd services/user-service && mvn spring-boot:run

# 7. Record Service
cd services/record-service && mvn spring-boot:run

# 8. Dashboard Service
cd services/dashboard-service && mvn spring-boot:run
```

> Each command runs in its own terminal tab.

## Default Admin

Created automatically on auth-service startup:

| Field    | Value      |
|----------|------------|
| Username | admin      |
| Password | admin123   |
| Role     | ADMIN      |

## API Endpoints (via Gateway :8080)

### Auth
| Method | Path                    | Access |
|--------|-------------------------|--------|
| POST   | /api/auth/register      | Public |
| POST   | /api/auth/login         | Public |

### Users
| Method | Path            | Access |
|--------|-----------------|--------|
| POST   | /api/users      | ADMIN  |
| GET    | /api/users      | ADMIN  |
| GET    | /api/users/{id} | ADMIN  |
| PUT    | /api/users/{id} | ADMIN  |
| DELETE | /api/users/{id} | ADMIN  |

### Financial Records
| Method | Path              | Access                   |
|--------|-------------------|--------------------------|
| POST   | /api/records      | ANALYST, ADMIN           |
| GET    | /api/records      | VIEWER, ANALYST, ADMIN   |
| GET    | /api/records/{id} | VIEWER, ANALYST, ADMIN   |
| PUT    | /api/records/{id} | ANALYST, ADMIN           |
| DELETE | /api/records/{id} | ADMIN                    |

### Dashboard
| Method | Path                       | Access                 |
|--------|----------------------------|------------------------|
| GET    | /api/dashboard/summary     | VIEWER, ANALYST, ADMIN |
| GET    | /api/dashboard/categories  | VIEWER, ANALYST, ADMIN |
| GET    | /api/dashboard/trends      | ANALYST, ADMIN         |
| GET    | /api/dashboard/recent      | VIEWER, ANALYST, ADMIN |

## Swagger UI (per service)

| Service          | URL                                    |
|------------------|----------------------------------------|
| auth-service     | http://localhost:8081/swagger-ui.html  |
| user-service     | http://localhost:8082/swagger-ui.html  |
| record-service   | http://localhost:8083/swagger-ui.html  |
| dashboard-service| http://localhost:8084/swagger-ui.html  |
| Eureka Dashboard | http://localhost:8761                  |

## MySQL Password Configuration

Each service `application.yml` has:
```yaml
spring:
  datasource:
    password:       ← leave blank if no MySQL password
```

Update all 4 service YMLs with your password before running.

## Key Design Decisions

1. **Shared jars not shared code** — common modules are compiled into versioned jars and declared as Maven dependencies, not copied between services
2. **Gateway-level JWT validation** — downstream services trust `X-Username`, `X-User-Role`, `X-User-Id` headers injected by the gateway
3. **Each service's own security config** — even though the gateway validates tokens, each service independently validates using `common-security` for defence-in-depth
4. **Dashboard reads record_service DB** — avoids inter-service HTTP calls for analytics; reads directly from the same MySQL schema (read-only, `ddl-auto=none`)
5. **Soft delete** — records are never hard-deleted; `deleted=true` flag preserves audit trail
6. **No Lombok** — explicit constructors, getters, setters, and builder patterns for transparency and IDE compatibility

