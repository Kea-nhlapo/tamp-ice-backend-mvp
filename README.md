# Truck Asset Matchmaking Platform (TAMP)

TAMP is a planned backend MVP that connects Freight Owners with Transporters that have suitable truck capacity. Administrators will oversee users, compliance metadata, disputes, audit information and basic metrics.

## Assessment purpose

This repository is an individual Industrial Computing Engineering (ICE) backend assessment. The objective is to demonstrate a clear, secure and testable API supporting the core freight matchmaking journey.

## MVP business journey

1. A Freight Owner or Transporter registers and signs in. Administrator accounts come from synthetic MVP seed data.
2. A Freight Owner posts a cargo load.
3. A Transporter posts an available truck.
4. The platform evaluates transparent matching rules and explains eligible matches.
5. A party accepts or rejects a match, with the decision recorded.
6. An accepted match produces a digital receipt.
7. The trip advances through mock tracking statuses.
8. The parties rate one another after completion.
9. An Administrator reviews platform activity and exceptions.

## User roles

- **Freight Owner:** manages cargo loads, match decisions, tracking and ratings.
- **Transporter:** manages truck availability, match decisions, tracking and ratings.
- **Administrator:** manages users, compliance metadata, disputes, audit events and metrics.

Public registration accepts only `FREIGHT_OWNER` and `TRANSPORTER`. The public endpoint must reject `ADMIN`; Administrator accounts are created through synthetic seed data for this MVP.

## Planned technology stack

Java 21, Spring Boot, Maven, PostgreSQL, Spring Data JPA, Spring Security, JWT authentication, Bean Validation, Swagger/OpenAPI, JUnit 5, Mockito, H2 for isolated automated tests and GitHub Actions.

## Planned architecture

One layered Spring Boot application will expose REST endpoints through controllers, coordinate business rules in services and persist data through Spring Data JPA repositories. Security will use JWT authentication and role-based access control. See [Architecture](docs/architecture.md).

## Planned API modules

Authentication, current-user profiles, compliance metadata, cargo loads, available trucks, rule-based matching, match decisions, receipts, mock tracking, ratings, disputes and administration.

## Current project status

**Database foundation implemented.** The Maven project builds with Java 21, starts with an in-memory H2 database, exposes a health endpoint and provides Swagger/OpenAPI documentation. Flyway creates the MVP schema and loads synthetic seed data. Business endpoints and JWT authentication have not been implemented yet.

## Setup

Requirements:

- Java 21

Run the application from the repository root:

```powershell
.\mvnw.cmd spring-boot:run
```

Health check:

```text
http://localhost:8080/actuator/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

### Database and seed data

The default local setup uses an in-memory H2 database. Flyway automatically runs the versioned migrations in `src/main/resources/db/migration` when the application starts.

- `V1__create_mvp_schema.sql` creates the MVP tables, relationships, constraints and indexes.
- `V2__seed_synthetic_data.sql` inserts reproducible synthetic users and sample records.
- Restarting the application resets the in-memory H2 database.
- PostgreSQL can be enabled with the `postgres` Spring profile after setting `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.

All seed records are synthetic and must not be treated as real people or operational data. Real secrets must not be committed.

## Testing

Run the automated tests from the repository root:

```powershell
.\mvnw.cmd test
```

The current scaffold includes:

- A Spring application context test.
- A health endpoint test that expects HTTP 200.
- A migration and synthetic seed-data integration test.
- A repository relationship integration test.
- A database uniqueness-constraint integration test.

The planned business-journey tests remain listed in the [Testing Summary](docs/testing-summary.md) and will be implemented with the related features.

## Documentation

- [Solution Overview](docs/solution-overview.md)
- [Architecture](docs/architecture.md)
- [Requirements Traceability](docs/requirements-traceability.md)
- [Planned API Contract](docs/api-contract.md)
- [Testing Summary](docs/testing-summary.md)
- [Planned Demo Guide](docs/demo-guide.md)
- [Known Limitations](docs/known-limitations.md)
- [Two-Day Work Plan](docs/work-plan.md)

## Scope boundaries

The MVP excludes a frontend, microservices, external GPS, payments, messaging integrations, production signatures, AI matching, route optimisation and production deployment infrastructure. Swagger/OpenAPI will be the primary demonstration interface.

All project and demonstration data will be synthetic or mock data. Real personal or operational data must not be used.

## Deadline

Submission and demonstration deadline: **Thursday, 30 July 2026**.
