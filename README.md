# CourierPay — Internal Courier Earnings & Payout System

CourierPay is a backend project for managing courier earnings, commissions, balances, and internal payouts.

## Tech stack

- Java 21
- Spring Boot 3.5.14
- Spring Web, Spring Security, Spring Data JPA
- PostgreSQL
- Liquibase
- Kafka
- ShedLock
- JWT authentication
- Swagger/OpenAPI
- Docker Compose
- JUnit, Spring Boot Test, Testcontainers-ready dependencies

## Main business flow

1. Admin creates a company with a commission rate.
2. User registers as ADMIN, COMPANY_MANAGER, or COURIER.
3. Admin/company manager links a courier user to a company.
4. Admin/company manager creates an earning.
5. The system calculates commission and net amount.
6. `EARNING_CREATED` event is published to Kafka.
7. Consumer or scheduled job processes pending earnings.
8. Courier balance is credited with the net amount.
9. Courier requests payout.
10. Admin approves payout.
11. Courier balance is debited internally.

## Run locally

```bash
docker compose up -d
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
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

Use the returned token as:

```text
Authorization: Bearer <token>
```

### Create company

```http
POST /api/v1/companies
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Frankfurt Fleet GmbH",
  "commissionRate": 10.00
}
```

### Create courier

```http
POST /api/v1/couriers
Authorization: Bearer <token>
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
Authorization: Bearer <token>
Content-Type: application/json

{
  "courierId": 1,
  "grossAmount": 100.00,
  "workDate": "2026-05-03",
  "idempotencyKey": "earning-2026-05-03-courier-1-001"
}
```

### Request payout

```http
POST /api/v1/payouts
Authorization: Bearer <token>
Content-Type: application/json

{
  "courierId": 1,
  "amount": 50.00
}
```

### Approve payout

```http
POST /api/v1/payouts/1/approve
Authorization: Bearer <token>
```
