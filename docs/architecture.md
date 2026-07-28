# Planned Architecture

TAMP will use a layered architecture inside one Spring Boot application.

```mermaid
flowchart LR
    Client[Swagger / Postman] --> Controllers[Spring Boot controllers]
    Controllers --> Services[Services]
    Services --> Repositories[Spring Data JPA repositories]
    Repositories --> Database[(PostgreSQL)]
    Security[JWT security and RBAC] -. protects .-> Controllers
```

## Controller layer

Controllers will expose versioned REST resources under `/api`, bind request DTOs, trigger Bean Validation, obtain authenticated-user context and return suitable HTTP responses. They will not contain matching or persistence rules.

## Service layer

Services will implement ownership checks, state transitions, matching rules, receipt creation, tracking progression, rating eligibility, dispute handling, metrics and audit-event creation. Transactions will be defined around operations that update related records.

## Repository layer

Spring Data JPA repositories will provide entity persistence and focused queries. Query methods will support ownership filtering, eligible-truck lookup, administrative lists and aggregate counts without leaking database concerns into controllers.

## Database layer

PostgreSQL is planned for development and demonstration. Constraints, indexes, migrations and synthetic seed data will be added during implementation. H2 or another isolated database may be used for automated tests.

## Security layer

Spring Security will authenticate JWT bearer tokens and enforce Freight Owner, Transporter and Administrator permissions. Passwords will be hashed with BCrypt. Authentication failures and insufficient permissions will return consistent 401 and 403 responses.

## DTO and validation approach

Request and response DTOs will keep API contracts separate from entities. Bean Validation will cover required text, positive measurements, valid rating ranges and date-window rules. Services will enforce ownership, compatibility and valid transitions.

## Global exception handling

A `@RestControllerAdvice` is planned to map validation failures, missing records, conflicts, forbidden operations and invalid state transitions to a consistent structured error response. Its final schema will be documented after scaffolding.

## Audit logging approach

Important actions will create persisted `AuditEvent` records containing actor, action, entity type, entity identifier, timestamp and limited metadata. Planned audited actions include registration, compliance decisions, posting changes, matching, acceptance, rejection, tracking changes and disputes. Secrets and full sensitive request bodies will not be logged.

## Planned domain objects

- `User`
- `ComplianceDocument`
- `Load`
- `Truck`
- `Match`
- `Receipt`
- `TrackingEvent`
- `Rating`
- `Dispute`
- `AuditEvent`
