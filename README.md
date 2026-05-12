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
- Render deployment

## Recently added features

- **RBAC:** role-based access control for `ADMIN`, `COMPANY_MANAGER`, and `COURIER` users.
- **Custom exceptions:** centralized application exceptions and consistent API error responses.
- **Duplicate earning-event protection:** `EarningService` prevents duplicate processing of the same earning event.
- **Separated async services:** Kafka listener and scheduled pending-earning job were moved out of `EarningService` into separate services.
- **Refresh tokens:** login/register return access and refresh tokens; refresh uses token rotation.
- **Courier data ownership:** couriers can only access their own balance and payouts.
- **Render deployment:** the API is deployed as a Render web service.
- **Public status endpoints:** `/` and `/healthz` are publicly accessible for browser checks and health checks.

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

For Render, Kafka is temporarily disabled with:

```text
KAFKA_ENABLED=false
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
- Kafka: temporarily disabled in runtime config
- Auto-deploy: enabled on commit

Required Render environment variables:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<render-db-host>:5432/<database-name>
SPRING_DATASOURCE_USERNAME=<render-db-user>
SPRING_DATASOURCE_PASSWORD=<render-db-password>
JWT_SECRET=<base64-secret>
JWT_EXPIRATION_MINUTES=120
REFRESH_TOKEN_EXPIRATION_DAYS=7
KAFKA_ENABLED=false
```

Use Render's internal database hostname when the web service and database are in the same Render region.

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
