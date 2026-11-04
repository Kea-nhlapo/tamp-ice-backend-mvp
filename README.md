# Truck Asset Matchmaking Platform (TAMP)

[![CI](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/actions/workflows/ci.yml/badge.svg)](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/actions/workflows/ci.yml)

A REST API that matches freight owners who have a load to move with transporters who have a truck free. I built it on my own in July 2026 as the backend assessment for the Industrial Computing Engineering (ICE) programme, working to a two-day plan.

It's backend only. Swagger UI is the way to try it.

## What it does

1. A freight owner or transporter registers and logs in and gets a JWT.
2. The freight owner posts a cargo load. The transporter posts a truck that's available.
3. The freight owner asks for matches on a load. Each candidate truck comes back with the reasons it qualifies.
4. Either party accepts or rejects the match. Accepting it issues a digital receipt.
5. The trip moves through mock tracking statuses until it's complete, then the two parties rate each other.
6. An admin reviews users, compliance documents, disputes, audit events and platform metrics.

There are three roles: `FREIGHT_OWNER`, `TRANSPORTER` and `ADMIN`. Public registration only accepts the first two. Admin accounts come from seed data.

## How matching works

A truck is eligible for a load only when all four rules pass (`MatchingRules.java`):

- **Capacity:** the truck's capacity is at least the load's weight.
- **Truck type:** frozen or perishable cargo needs a refrigerated truck. Liquid or fuel needs a tanker, and machinery or construction material needs a flatbed. Anything else goes in a box truck.
- **Timing:** the truck's availability window overlaps the load's pickup window.
- **Location:** the truck's current location is the same as the load's origin. This compares city or area names, not real distances.

Every rule that passes is returned as a reason, so the API can explain a match instead of just returning a score.

## Stack

Java 21, Spring Boot 4, Spring Security with JWT (jjwt) and BCrypt, Spring Data JPA, Flyway, PostgreSQL (H2 in memory by default), springdoc OpenAPI, JUnit 5, Docker Compose and GitHub Actions.

Most endpoints are in `MvpController`, and the business rules are in `MvpService`. Persistence goes through one Spring Data repository per entity. Security is stateless: a filter checks the JWT on each request, and role rules in `SecurityConfig` lock down admin routes, load creation and truck creation. [Architecture](docs/architecture.md) has more detail.

## Run it

You need Java 21.

The app needs a Base64 JWT signing secret. In PowerShell, create one for the current session:

```powershell
$secretBytes = New-Object byte[] 32
$secretGenerator = [Security.Cryptography.RandomNumberGenerator]::Create()
$secretGenerator.GetBytes($secretBytes)
$secretGenerator.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($secretBytes)
```

On Linux or macOS: `export JWT_SECRET=$(openssl rand -base64 32)`.

Then start the app from the repository root:

```powershell
.\mvnw.cmd spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Health: http://localhost:8080/actuator/health

By default it uses an in-memory H2 database, which resets on every restart. Flyway creates the schema and loads synthetic seed data on startup (`src/main/resources/db/migration`).

### Demo logins

All three use the password `Password123!`:

- `owner@tamp.test` (freight owner)
- `transporter@tamp.test` (transporter)
- `admin@tamp.test` (admin)

Log in with `POST /api/auth/login`, paste the token into Swagger's **Authorize** dialog, and follow the [Demo Guide](docs/demo-guide.md) to step through the full journey.

### With PostgreSQL in Docker

You need Docker with Compose.

```bash
cp .env.example .env
```

In `.env`, set `POSTGRES_PASSWORD` and set `JWT_SECRET` to the output of `openssl rand -base64 32`. Then run:

```bash
docker compose up --build
```

This starts PostgreSQL 17 and the app with the `postgres` profile. Swagger is on the same URL once both services are healthy. `docker compose down` keeps the data in the `postgres-data` volume, and `docker compose down --volumes` wipes it.

To point a local run at your own PostgreSQL instead, use the `postgres` Spring profile and set `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.

Tokens expire after an hour, and a restart with a new `JWT_SECRET` makes old tokens invalid, so log in again after restarting.

## Tests

```powershell
.\mvnw.cmd test
```

There are 31 tests across 9 classes. They run against H2 and cover:

- startup, health and the OpenAPI document
- the Flyway migrations and database constraints
- registration and login, including the rule that the public endpoint rejects `ADMIN`
- role restrictions on protected routes
- every matching rule
- the full journey from posting a load to a completed, rated trip
- guards against double-booking a truck and against editing a load or truck once its match is accepted

CI runs `./mvnw verify` on every push and pull request to `main`.

## Limits

This is an assessment MVP, so a lot is mocked or left out on purpose. Compliance documents are metadata only and tracking is simulated. There are no payments, GPS, notifications or frontend, and it isn't deployed anywhere. All data is synthetic. The full list is in [Known Limitations](docs/known-limitations.md).

## Docs

- [Solution Overview](docs/solution-overview.md)
- [Architecture](docs/architecture.md)
- [API Contract](docs/api-contract.md)
- [Requirements Traceability](docs/requirements-traceability.md)
- [Testing Summary](docs/testing-summary.md)
- [Demo Guide](docs/demo-guide.md)
- [Known Limitations](docs/known-limitations.md)
- [Two-Day Work Plan](docs/work-plan.md)
