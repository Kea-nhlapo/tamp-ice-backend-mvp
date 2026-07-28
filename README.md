# Truck Asset Matchmaking Platform (TAMP)

TAMP is a planned backend MVP that connects Freight Owners with Transporters that have suitable truck capacity. Administrators will oversee users, compliance metadata, disputes, audit information and basic metrics.

## Assessment purpose

This repository is an individual Industrial Computing Engineering (ICE) backend assessment. The objective is to demonstrate a clear, secure and testable API supporting the core freight matchmaking journey.

## MVP business journey

1. A user registers with a role and signs in.
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

## Planned technology stack

Java 21, Spring Boot, Maven, PostgreSQL, Spring Data JPA, Spring Security, JWT authentication, Bean Validation, Swagger/OpenAPI, JUnit 5, Mockito, H2 for isolated automated tests and GitHub Actions.

## Planned architecture

One layered Spring Boot application will expose REST endpoints through controllers, coordinate business rules in services and persist data through Spring Data JPA repositories. Security will use JWT authentication and role-based access control. See [Architecture](docs/architecture.md).

## Planned API modules

Authentication, current-user profiles, compliance metadata, cargo loads, available trucks, rule-based matching, match decisions, receipts, mock tracking, ratings, disputes and administration.

## Current project status

**Repository foundation and planning only.** Application scaffolding, endpoints, persistence, security and automated tests have not started.

## Setup

**To be completed after project scaffolding.**

No installation commands are provided yet because the Spring Boot project does not exist.

## Testing

**To be completed during implementation.**

The planned coverage is recorded in the [Testing Summary](docs/testing-summary.md). No tests are currently claimed as implemented or run.

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
