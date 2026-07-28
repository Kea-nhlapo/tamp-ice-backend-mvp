# GitHub Issue Backlog

The linked GitHub issues are the implementation work queue. Nothing below is claimed as implemented.

## 1. [chore: Scaffold Spring Boot application and API documentation](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/2)

**Related requirements:** Foundation for FR-01–FR-12.

**User story:** As the developer, I want a minimal buildable application so that each MVP capability can be added and demonstrated consistently.

**Scope**

- Create a Java 21 Spring Boot Maven project.
- Add Spring Web, Data JPA, Security, Validation, PostgreSQL, H2, Test and OpenAPI dependencies.
- Add a health endpoint, Swagger configuration and clear package structure.
- Confirm that the application builds.

**Acceptance criteria**

- [ ] Maven build succeeds.
- [ ] Application starts.
- [ ] Health endpoint returns HTTP 200.
- [ ] Swagger page loads.
- [ ] No secrets are committed.

**Tests required:** Add a context-load or smoke test and verify the health endpoint.

**Documentation updates:** README setup, API contract and demo guide.

**Out of scope:** Domain workflows, production deployment and optional infrastructure.

## 2. [feat: Create database model, migrations and seed data](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/3)

**Related requirements:** FR-02–FR-12.

**User story:** As the developer, I want a constrained relational model so that the MVP journey can persist consistent synthetic data.

**Scope**

- Configure PostgreSQL and a development migration strategy.
- Create MVP entities, relationships, constraints and necessary indexes.
- Add synthetic seed users and sample records.
- Model `User`: ID, name, email, hashed password, role, compliance status, rating.
- Model `ComplianceDocument`: ID, user, document type, document name/reference, submitted date, status, reviewed date.
- Model `Load`: ID, owner, origin, destination, cargo type, weight, volume, pickup start/end, status.
- Model `Truck`: ID, transporter, type, capacity, current location, availability start/end, status.
- Model `Match`: ID, load, truck, score, reasons, status, created date.
- Model `Receipt`: contract ID, match, actor, decision, timestamp, IP address, user-agent.
- Model `TrackingEvent`: match/trip, status or coordinates, timestamp.
- Model `Rating`: trip/match, reviewer, reviewed party, score, comment, timestamp.
- Model `Dispute`: match, creator, reason, status, timestamp.
- Model `AuditEvent`: actor, action, entity type, entity ID, timestamp, metadata.

**Acceptance criteria**

- [ ] Migrations create the planned schema on a clean database.
- [ ] Relationships and MVP constraints are enforced.
- [ ] Seed data is synthetic and reproducible.
- [ ] No credential or real personal data is committed.

**Tests required:** Migration smoke test, repository relationship checks and key constraint tests.

**Documentation updates:** Architecture, README database notes and seed-data instructions.

**Out of scope:** Production-scale tuning, real compliance files and live operational data.

## 3. [feat: Implement authentication and role-based access](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/4)

**Related requirements:** FR-01, FR-12.

**User story:** As a platform user, I want secure registration and login so that I can access only capabilities allowed for my role.

**Scope**

- Implement registration and login.
- Limit public registration to `FREIGHT_OWNER` and `TRANSPORTER`.
- Reject any public registration request that selects `ADMIN`.
- Create MVP Administrator accounts through synthetic seed data.
- Hash passwords with BCrypt.
- Create and validate JWTs.
- Support login and authorisation for Freight Owner, Transporter and Admin roles.
- Return consistent 401 and 403 responses.
- Audit registration and relevant security actions.

**Acceptance criteria**

- [ ] Valid registration and login succeed.
- [ ] Public registration accepts `FREIGHT_OWNER` and `TRANSPORTER`.
- [ ] Public registration rejects `ADMIN`.
- [ ] A synthetic seeded Administrator can log in.
- [ ] Duplicate email is rejected.
- [ ] Plain-text passwords are never stored or returned.
- [ ] Invalid or missing tokens return 401.
- [ ] Authenticated users without permission receive 403.

**Tests required:** Freight Owner and Transporter registration, rejected public Admin registration, seeded Admin login, duplicate email, hashing, token validation and role-access tests.

**Documentation updates:** API contract, traceability, testing summary and Swagger security notes.

**Out of scope:** OAuth, password recovery, MFA and external identity providers.

## 4. [feat: Implement user profiles and compliance metadata](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/5)

**Related requirements:** FR-02, FR-10, FR-12.

**User story:** As a user, I want to maintain my profile and submit compliance metadata so that an Administrator can review my basic eligibility.

**Scope**

- View and update the current profile.
- Submit mock compliance-document metadata.
- Allow an Administrator to approve or reject compliance.
- Enforce validation and ownership rules.
- Create audit events.

**Acceptance criteria**

- [ ] A user can read and update only allowed fields on their profile.
- [ ] Required compliance metadata is validated and persisted.
- [ ] Only an Administrator can change compliance status.
- [ ] Compliance decisions create audit events.

**Tests required:** Profile ownership, field validation, metadata submission, admin permission and audit tests.

**Documentation updates:** API contract, traceability, testing summary and demo guide.

**Out of scope:** Binary upload, external verification and certification.

## 5. [feat: Implement cargo load operations](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/6)

**Related requirements:** FR-03, FR-12.

**User story:** As a Freight Owner, I want to manage cargo loads so that suitable Transporters can be identified.

**Scope**

- Create, list, view and update loads.
- Validate origin, destination, cargo type, positive weight, positive volume and pickup window.
- Enforce role and ownership permissions.
- Audit load creation and update.

**Acceptance criteria**

- [ ] A Freight Owner can create and retrieve a valid load.
- [ ] Invalid measurements or pickup windows are rejected.
- [ ] A Transporter cannot create a load.
- [ ] Only the owner can update a load.
- [ ] Creation and update produce audit events.

**Tests required:** Happy path, validation, role denial, ownership and missing-record tests.

**Documentation updates:** API contract, traceability, testing summary and Swagger examples.

**Out of scope:** Bulk imports, route optimisation and real geocoding.

## 6. [feat: Implement available truck operations](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/7)

**Related requirements:** FR-04, FR-12.

**User story:** As a Transporter, I want to manage available trucks so that the platform can evaluate them against cargo loads.

**Scope**

- Create, list, view and update trucks.
- Validate truck type, positive capacity, location and availability window.
- Enforce role and ownership permissions.
- Audit truck creation and update.

**Acceptance criteria**

- [ ] A Transporter can create and retrieve a valid truck.
- [ ] Invalid capacity or availability windows are rejected.
- [ ] A Freight Owner cannot create a truck.
- [ ] Only the owning Transporter can update a truck.
- [ ] Creation and update produce audit events.

**Tests required:** Happy path, validation, role denial, ownership and missing-record tests.

**Documentation updates:** API contract, traceability, testing summary and Swagger examples.

**Out of scope:** Fleet telematics, maintenance management and bulk fleet imports.

## 7. [feat: Implement rule-based matchmaking](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/8)

**Related requirements:** FR-05, FR-12.

**User story:** As a Freight Owner, I want explainable truck recommendations so that I can choose a suitable available vehicle.

**Scope**

- Reject a truck when capacity is below load weight.
- Reject incompatible truck and cargo types.
- Reject non-overlapping availability windows.
- Reject a truck whose location does not match the load origin city or area.
- Return eligible matches with a score and human-readable reasons.
- Aim for a response below two seconds using sample data.

**Acceptance criteria**

- [ ] Each rejection rule is applied independently.
- [ ] An eligible result includes a score and readable reasons.
- [ ] Only relevant available trucks are considered.
- [ ] Match generation creates appropriate audit evidence.
- [ ] A basic sample-data timing check is recorded.

**Tests required:** One unit test for every rule, a valid-match test, combined-rule coverage and a basic timing check.

**Documentation updates:** Architecture, API contract, traceability, testing summary and demo guide.

**Out of scope:** AI, machine learning, live distance services and advanced route optimisation.

## 8. [feat: Implement match acceptance, rejection and receipts](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/9)

**Related requirements:** FR-06, FR-07, FR-12.

**User story:** As a party to a match, I want to record a decision and receive confirmation after acceptance so that the engagement has a transparent record.

**Scope**

- Add accept and reject endpoints.
- Enforce valid match status transitions.
- Save the decision and actor.
- Generate a receipt on acceptance.
- Store contract ID, actor, timestamp, IP address and user-agent.
- Create audit events.
- Prevent duplicate acceptance and invalid transitions.

**Acceptance criteria**

- [ ] An authorised party can accept or reject an eligible match.
- [ ] Acceptance creates one receipt with the required fields.
- [ ] Repeated or conflicting decisions are rejected.
- [ ] Match, receipt and audit records remain transactionally consistent.

**Tests required:** Accept, reject, receipt fields, authorisation, duplicate decision, invalid transition and transaction tests.

**Documentation updates:** API contract, traceability, testing summary and demo guide.

**Out of scope:** Production electronic signatures, payments and legal contract generation.

## 9. [feat: Implement mock tracking, ratings and disputes](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/10)

**Related requirements:** FR-08, FR-09, FR-10, FR-12.

**User story:** As a matched party, I want simulated trip progress and post-trip feedback so that the core journey can be demonstrated without external integrations.

**Scope**

- Advance trips through `ACCEPTED`, `DRIVER_ASSIGNED`, `EN_ROUTE_TO_PICKUP`, `CARGO_COLLECTED`, `IN_TRANSIT`, `DELIVERED` and `COMPLETED`.
- Allow ratings only after completion.
- Require rating scores from 1 to 5.
- Create and retrieve disputes.
- Create audit events.

**Acceptance criteria**

- [ ] Authorised parties can add and read mock tracking events.
- [ ] Invalid status progression is rejected.
- [ ] Ratings before completion or outside 1–5 are rejected.
- [ ] Relevant parties can create and retrieve disputes.
- [ ] Tracking, ratings and disputes create audit evidence.

**Tests required:** Status progression, invalid transition, completed-trip rating, score validation, party access and dispute tests.

**Documentation updates:** API contract, traceability, testing summary, demo guide and known limitations.

**Out of scope:** Live GPS, maps, notifications and automated dispute resolution.

## 10. [feat: Implement Admin controls and platform metrics](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/11)

**Related requirements:** FR-10, FR-11, FR-12.

**User story:** As an Administrator, I want focused oversight endpoints so that I can review platform activity and handle exceptions.

**Scope**

- List users and update compliance status.
- View disputes and change dispute status.
- View audit events.
- Return counts for users, loads, trucks, matches, accepted matches, completed trips and open disputes.
- Reject non-admin users.

**Acceptance criteria**

- [ ] Administrator endpoints return the planned records or counts.
- [ ] Compliance and dispute changes validate allowed statuses.
- [ ] Administrative changes create audit events.
- [ ] Non-admin users receive 403.

**Tests required:** Admin happy paths, aggregate counts, status validation, audit creation and non-admin denial.

**Documentation updates:** API contract, traceability, testing summary, Swagger descriptions and demo guide.

**Out of scope:** Advanced analytics, dashboards, exports and production monitoring.

## 11. [test: Add unit, integration and API error tests](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/12)

**Related requirements:** FR-01–FR-12.

**User story:** As the assessor, I want focused automated evidence so that critical behaviour, validation and permissions can be verified repeatably.

**Scope**

- Complete approximately 12–15 meaningful automated tests.
- Add service unit tests and selected controller or integration tests.
- Cover happy paths, validation errors, permissions and missing records.
- Add consistent structured API errors through global exception handling.
- Update the testing summary using real results.

**Acceptance criteria**

- [ ] The selected test suite passes in an isolated test environment.
- [ ] Critical matching and access-control rules have direct evidence.
- [ ] Validation and missing-record responses use the documented error structure.
- [ ] Actual results and statuses are recorded accurately.

**Tests required:** The test suite is the deliverable; include regression coverage for defects discovered during implementation.

**Documentation updates:** Testing summary, API error contract, traceability and README test commands.

**Out of scope:** Exhaustive performance, penetration, DAST and production load testing.

## 12. [chore: Add CI, final documentation and demo preparation](https://github.com/Kea-nhlapo/tamp-ice-backend-mvp/issues/13)

**Related requirements:** Verification and presentation evidence for FR-01–FR-12.

**User story:** As the developer presenting the assessment, I want repeatable checks and accurate instructions so that the solution can be reviewed and demonstrated reliably.

**Scope**

- Add a GitHub Action that builds and tests the Maven application.
- Finalise README setup instructions, test credentials and seed-data instructions.
- Document the Swagger URL.
- Review requirements traceability and the testing summary.
- Update the demo guide and known limitations.
- Run a final end-to-end test.
- Prepare a presentation rehearsal checklist.

**Acceptance criteria**

- [ ] CI builds and tests the Maven project.
- [ ] A clean local setup can follow the README.
- [ ] Only synthetic credentials and data are documented.
- [ ] Traceability and testing records match actual implementation.
- [ ] The planned demo completes in rehearsal.

**Tests required:** Run the complete automated suite and a documented manual end-to-end rehearsal.

**Documentation updates:** README and all planning documents must be reconciled with the implemented behaviour.

**Out of scope:** Production deployment, advanced security pipelines and optional infrastructure improvements.
