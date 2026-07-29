# Requirements Traceability

This table links each requirement to its current implementation and test evidence.

| Requirement ID | Requirement summary | Priority | Endpoint/service | Test evidence | Status |
|---|---|---|---|---|---|
| FR-01 | Public registration for Freight Owner and Transporter; login for all roles; seeded Admin accounts | Must | `/api/auth/register`, `/api/auth/login`; authentication service and synthetic Admin seed data | Registration, hashing, authentication, public Admin-registration rejection and role-access tests | Implemented |
| FR-02 | Basic user identity and compliance-document metadata | Must | `/api/users/me`; compliance service and admin compliance endpoint | Profile ownership, metadata validation and admin-decision tests | Implemented |
| FR-03 | Freight Owner can create and view cargo loads | Must | `/api/loads`; load service | Load creation, viewing, validation and role tests | Implemented |
| FR-04 | Transporter can create and view available trucks | Must | `/api/trucks`; truck service | Truck creation, viewing, validation and role tests | Implemented |
| FR-05 | Matching checks capacity, compatibility, location and availability | Must | `/api/loads/{loadId}/matches`; matching service | A unit test for each rejection rule and a valid-match test | Implemented |
| FR-06 | Users can accept or reject matches and decisions are logged | Must | `/api/matches/{id}/accept`, `/reject`; decision service | Transition, duplicate-decision and audit tests | Implemented |
| FR-07 | Accepted matches produce a digital receipt | Must | `/api/matches/{id}/receipt`; receipt service | Receipt fields and acceptance transaction tests | Implemented |
| FR-08 | Trips use simulated tracking statuses or coordinates | Must | `/api/matches/{id}/tracking-events`; tracking service | Allowed progression and invalid-transition tests | Implemented |
| FR-09 | Parties can rate one another after completion | Should | `/api/matches/{id}/ratings`; rating service | Completion requirement, score range and party tests | Implemented |
| FR-10 | Admin manages users, compliance and disputed or flagged items | Must | `/api/admin/users`, compliance and dispute endpoints | Admin permission and state-update tests | Implemented |
| FR-11 | Admin views basic platform metrics | Must | `/api/admin/metrics`; metrics service | Aggregate-count and non-admin rejection tests | Implemented |
| FR-12 | Important actions are stored in an audit trail | Must | `/api/admin/audit-events`; audit service | Audit creation and admin-read tests | Implemented |
