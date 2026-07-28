# Solution Overview

## Business problem

Freight Owners need a quicker way to find suitable transport capacity, while Transporters need opportunities to use available trucks and reduce empty journeys. Both parties need understandable matching decisions and a reliable record of actions.

## MVP purpose

The MVP will demonstrate the core journey from registration through posting, rule-based matching, acceptance, mock trip progress and administrative oversight. It is an individual backend assessment and is intentionally limited to work that can be delivered and explained within two working days.

## Backend scope

The backend will provide REST APIs, persistence, validation, authentication, role restrictions, matching rules, digital receipts, simulated tracking, ratings, disputes, audit records and simple administrative counts. Swagger/OpenAPI will support demonstration and API exploration.

## User roles

- **Freight Owner:** posts and manages cargo loads, reviews matches, records decisions, follows trips and submits ratings.
- **Transporter:** posts and manages trucks, reviews matches, records decisions, updates trip progress and submits ratings.
- **Administrator:** reviews users and compliance metadata, manages disputes, reads audit events and views metrics.

Public registration is limited to `FREIGHT_OWNER` and `TRANSPORTER`. The API must reject attempts to select `ADMIN`; synthetic seed data will provide Administrator accounts for the MVP.

## Main workflow

1. Register as a Freight Owner or Transporter, or log in with a synthetic seeded Administrator account.
2. Create a load and a compatible available truck.
3. Generate eligible matches using explicit rules.
4. Accept a match and create a receipt, or reject it with a logged decision.
5. Advance simulated tracking to completion.
6. Submit a rating and demonstrate administrator oversight.

## Technology choices

- Java 21 and Spring Boot for the application.
- Maven for dependency management and builds.
- PostgreSQL with Spring Data JPA for persistent relational data.
- Spring Security, JWT and BCrypt for access control and credentials.
- Bean Validation and global exception handling for predictable API input and errors.
- Swagger/OpenAPI for the live backend demonstration.
- JUnit 5, Mockito and H2 for automated testing.
- GitHub Actions for build and test checks after scaffolding.

## Important design decisions

This will be one clean Spring Boot application. A layered design keeps HTTP handling, business rules and persistence separate without the delivery overhead of distributed services. This approach supports speed, clarity and reliability for the assessment.

Matching will be rule-based and explainable. City or area equality will stand in for distance calculations. Compliance files and trip tracking will be represented by mock metadata or statuses. Important state changes will create audit events.

## Intentionally excluded

Microservices, a Python matching service, reverse proxies, AWS resources, Kubernetes, DAST pipelines, a frontend, JOptionPane, live GPS, payments, external messaging, production signatures, AI, advanced route optimisation and production cloud deployment are outside this MVP.
