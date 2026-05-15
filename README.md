# CourierPay — Internal Courier Earnings & Payout System

CourierPay is a Spring Boot backend API for managing courier earnings, company commissions, balances, and internal payout workflows.

**Live Render URL:** https://courierpay.onrender.com

**SWAGGER:** https://courierpay.onrender.com/swagger-ui/index.html

Public check endpoints:

```text
GET https://courierpay.onrender.com/
GET https://courierpay.onrender.com/healthz
```

The root endpoint returns a simple API status response. Most business endpoints are protected by JWT authentication.

## Tech stack

- Java 21
- Spring Boot 3.5.14
- Spring Web, Spring Security, Spring Data JPA
- PostgreSQL
- Liquibase
- Kafka
- ShedLock
- JWT access tokens and refresh-token rotation
- Swagger/OpenAPI
- Docker and Docker Compose
- GitHub Actions CI
- JUnit 5, Mockito, MockMvc, Spring Boot Test, Data JPA Test, H2 test database
- Render deployment
- External managed Kafka service for deployed async event processing

## Recently added features

- **RBAC:** role-based access control for `ADMIN`, `COMPANY_MANAGER`, and `COURIER` users.
- **Custom exceptions:** centralized application exceptions and consistent API error responses.
- **Duplicate earning-event protection:** `EarningService` prevents duplicate processing of the same earning event.
- **Separated async services:** Kafka listener and scheduled pending-earning job were moved out of `EarningService` into separate services.
- **Refresh tokens:** login/register return access and refresh tokens; refresh uses token rotation.
- **Courier data ownership:** couriers can only access their own balance and payouts.
- **Render deployment:** the API is deployed as a Render web service.
- **External Kafka service:** deployed runtime can connect to a managed external Kafka broker, such as Aiven for Apache Kafka, instead of relying on local Docker Kafka.
- **Public status endpoints:** `/` and `/healthz` are publicly accessible for browser checks and health checks.
- **Automated test coverage:** added service unit tests, MVC slice tests, JPA repository slice tests, exception tests, and a full `@SpringBootTest` application wiring test.
- **Dedicated test profile:** added `application-test.yml` with H2 PostgreSQL-mode database, disabled Liquibase, and disabled Kafka for repeatable CI-friendly tests.

## Main business flow

1. Admin creates a company with a commission rate.
2. User registers as `ADMIN`, `COMPANY_MANAGER`, or `COURIER`.
3. Admin/company manager links a courier user to a company.
4. Admin/company manager creates an earning.
5. The system calculates commission and net amount.
6. An `EARNING_CREATED` event can be published to Kafka.
7. Kafka listener or scheduled job processes pending earnings.
8. Courier balance is credited with the net amount.
9. Courier requests payout.
10. Admin approves or rejects payout.
11. If approved, courier balance is debited internally.

## Security and access rules

CourierPay uses JWT authentication and RBAC.

General access rules:

- `ADMIN` can manage companies and approve/reject payouts.
- `ADMIN` and `COMPANY_MANAGER` can manage couriers and earnings.
- `COURIER` can request payouts.
- `COURIER` can only access their own balance and payouts.
- Public endpoints:
  - `/`
  - `/healthz`
  - `/api/v1/auth/**`
  - Swagger/OpenAPI endpoints

A `403 Forbidden` response on a protected endpoint usually means the API is reachable, but the request is missing a valid token or the user role is not allowed.

## Run locally

Start infrastructure:

```bash
docker compose up -d
```

Run the application:

```bash
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Local status endpoints:

```text
http://localhost:8080/
http://localhost:8080/healthz
```

## Configuration

Important environment variables:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/courierpay
SPRING_DATASOURCE_USERNAME=courierpay
SPRING_DATASOURCE_PASSWORD=courierpay
JWT_SECRET=<base64-secret>
JWT_EXPIRATION_MINUTES=120
REFRESH_TOKEN_EXPIRATION_DAYS=7
KAFKA_ENABLED=false
```

For local development or CI, Kafka can stay disabled:

```text
KAFKA_ENABLED=false
```

For the deployed Render environment, Kafka can be enabled by connecting the app to an external managed Kafka service. This project was prepared to use an external Kafka broker, for example Aiven for Apache Kafka, because Render runs the Spring Boot API as a web service and does not provide Kafka inside the application container.

Required Kafka deployment variables:

```text
KAFKA_ENABLED=true
SPRING_KAFKA_BOOTSTRAP_SERVERS=<external-kafka-host>:<port>
EARNING_CREATED_TOPIC=earning.created
PAYOUT_REQUESTED_TOPIC=payout.requested
SPRING_KAFKA_PROPERTIES_SECURITY_PROTOCOL=SASL_SSL
SPRING_KAFKA_PROPERTIES_SASL_MECHANISM=PLAIN
SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username="<username>" password="<password>";
```

The deployed Kafka service should contain these topics:

```text
earning.created
payout.requested
```

## Render deployment

The application is deployed on Render:

```text
https://courierpay.onrender.com
```

Render setup summary:

- Service type: Web Service
- Runtime: Docker
- Branch: `develop`
- Database: Render PostgreSQL
- Kafka: external managed Kafka service, configured through Render environment variables
- Auto-deploy: enabled on commit

Required Render environment variables:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<render-db-host>:5432/<database-name>
SPRING_DATASOURCE_USERNAME=<render-db-user>
SPRING_DATASOURCE_PASSWORD=<render-db-password>
JWT_SECRET=<base64-secret>
JWT_EXPIRATION_MINUTES=120
REFRESH_TOKEN_EXPIRATION_DAYS=7
KAFKA_ENABLED=true
SPRING_KAFKA_BOOTSTRAP_SERVERS=<external-kafka-host>:<port>
EARNING_CREATED_TOPIC=earning.created
PAYOUT_REQUESTED_TOPIC=payout.requested
SPRING_KAFKA_PROPERTIES_SECURITY_PROTOCOL=SASL_SSL
SPRING_KAFKA_PROPERTIES_SASL_MECHANISM=PLAIN
SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username="<username>" password="<password>";
```

Use Render's internal database hostname when the web service and database are in the same Render region.


## External Kafka deployment

Kafka is optional locally but can be enabled in the deployed environment through an external managed Kafka provider. The current deployment setup is designed for a managed broker such as Aiven for Apache Kafka.

The application publishes domain events to:

```text
earning.created
payout.requested
```

When Kafka is enabled, the `earning.created` topic is consumed by the earning processor listener, allowing earning processing and courier balance updates to run asynchronously. The scheduled pending-earning processor remains useful as a safety fallback for unprocessed records.

In Render, do not use `localhost:9092`. The Render container must connect to the external Kafka bootstrap server provided by the managed Kafka service.


## Testing

The project includes layered automated tests for both business logic and Spring wiring.

```bash
mvn test
```

Current test coverage includes:

- **Full application wiring test:** `CourierPayApplicationTests` uses `@SpringBootTest` with the `test` profile to boot the full Spring context, verify controllers, services, repositories, security/JWT beans, ShedLock config, and the `/healthz` endpoint through `MockMvc`.
- **MVC slice test:** `CompanyControllerTest` uses `@WebMvcTest` and `MockMvc` to verify company creation validation, JSON responses, filtering, pagination, and controller-to-service argument passing. Security collaborators are mocked so the controller layer can be tested without loading the full app.
- **Repository slice tests:** `CompanyRepositoryTest` and `UserRepositoryTest` use `@DataJpaTest` with H2 in PostgreSQL compatibility mode and `ddl-auto=create-drop` to validate repository search, email lookup, and existence checks without requiring PostgreSQL in CI.
- **Service unit tests:** `BalanceServiceTest` and `TransactionServiceTest` use JUnit 5 and Mockito to verify balance access checks, credit/debit behavior, insufficient-balance protection, and transaction persistence details.
- **Exception tests:** `ApplicationExceptionTest` verifies placeholder handling, localized message formatting, and fallback behavior when localization dependencies are missing.

The test profile lives in `src/test/resources/application-test.yml`. It keeps tests self-contained by using an in-memory H2 database, disabling Liquibase, and disabling Kafka runtime behavior through `app.kafka.enabled=false`.

## CI/CD

The project includes GitHub Actions CI. The expected flow is:

```text
git push origin develop
        ↓
GitHub Actions runs build/tests
        ↓
Render auto-deploys the Docker web service
        ↓
Liquibase applies database migrations on startup
```

## Example requests

### Register admin

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "admin@courierpay.com",
  "password": "password123",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

Example response:

```json
{
  "accessToken": "<access-token>",
  "refreshToken": "<refresh-token>",
  "tokenType": "Bearer"
}
```

Use the access token as:

```text
Authorization: Bearer <access-token>
```

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@courierpay.com",
  "password": "password123"
}
```

### Refresh token

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<refresh-token>"
}
```

The refresh endpoint uses refresh-token rotation: the old refresh token is revoked and a new refresh token is returned.

### Create company

```http
POST /api/v1/companies
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "name": "Frankfurt Fleet GmbH",
  "commissionRate": 10.00
}
```

### Create courier

```http
POST /api/v1/couriers
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "userId": 2,
  "companyId": 1,
  "phoneNumber": "+49123456789"
}
```

### Create earning

```http
POST /api/v1/earnings
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "courierId": 1,
  "grossAmount": 100.00,
  "workDate": "2026-05-03",
  "idempotencyKey": "earning-2026-05-03-courier-1-001"
}
```

### Get courier balance

```http
GET /api/v1/balances/couriers/1
Authorization: Bearer <access-token>
```

Couriers are restricted to their own `courierId`.

### Request payout

```http
POST /api/v1/payouts
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "courierId": 1,
  "amount": 50.00
}
```

Couriers can only request payouts for their own `courierId`.

### List payouts

```http
GET /api/v1/payouts
Authorization: Bearer <access-token>
```

Couriers only receive their own payouts. Admins and company managers keep broader access.

### Approve payout

```http
POST /api/v1/payouts/1/approve
Authorization: Bearer <access-token>
```

## Notes

- Opening `https://courierpay.onrender.com` in a browser should return the public root status response.
- Protected API endpoints require a valid JWT access token.
- Render free services may sleep after inactivity, so the first request can be slow.
