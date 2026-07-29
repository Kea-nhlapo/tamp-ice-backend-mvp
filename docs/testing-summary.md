# Testing Summary

This table records the automated evidence executed for the MVP.

## Scaffold checks

| Test ID | Area | Test description | Test type | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| S-01 | Application | Spring application context loads | Integration | Application starts for the test | Application context loaded | Passed |
| S-02 | Health | Health endpoint returns HTTP 200 | Integration | Health request succeeds | HTTP 200 returned | Passed |

## Business tests

| Test ID | Area | Test description | Test type | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| T-01 | Authentication | Registration succeeds | Integration | User is created and a successful response is returned | Freight Owner and Transporter registration succeeded | Passed |
| T-02 | Authentication | Duplicate email fails | Integration | Conflict response; no duplicate user | Duplicate email was rejected | Passed |
| T-03 | Security | Password is not stored as plain text | Unit/integration | Stored hash differs from submitted password and BCrypt verifies it | BCrypt hash stored and verified | Passed |
| T-04 | Loads | Freight Owner can create a load | Integration | Valid load is persisted for its owner | HTTP 201 returned and load persisted | Passed |
| T-05 | Loads | Transporter cannot create a load | Security integration | Request is rejected with 403 | HTTP 403 returned | Passed |
| T-06 | Loads | Invalid load weight is rejected | Validation integration | Request is rejected with 400 | HTTP 400 returned | Passed |
| T-07 | Trucks | Transporter can create a truck | Integration | Valid truck is persisted for its transporter | HTTP 201 returned and truck persisted | Passed |
| T-08 | Trucks | Freight Owner cannot create a truck | Security integration | Request is rejected with 403 | HTTP 403 returned | Passed |
| T-09 | Matching | Truck with insufficient capacity is rejected | Unit | Truck is ineligible with a capacity reason | Candidate was rejected | Passed |
| T-10 | Matching | Incompatible truck type is rejected | Unit | Truck is ineligible with a compatibility reason | Candidate was rejected | Passed |
| T-11 | Matching | Non-overlapping availability is rejected | Unit | Truck is ineligible with an availability reason | Candidate was rejected | Passed |
| T-12 | Matching | Wrong location is rejected | Unit | Truck is ineligible with a location reason | Candidate was rejected | Passed |
| T-13 | Matching | Valid truck produces a match with reasons | Unit | Eligible match includes a score and readable reasons | Four readable qualification reasons returned | Passed |
| T-14 | Acceptance | Accepting a match creates a receipt and audit event | Integration | Match, receipt and audit event are saved atomically | Receipt and audit event persisted | Passed |
| T-15 | Administration | Non-admin user cannot access Admin metrics | Security integration | Request is rejected with 403 | Admin route returned 403 | Passed |

## Additional authentication tests

| Test ID | Area | Test description | Test type | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| T-16 | Authentication | Public Admin registration is rejected | Integration | Request is rejected with 400 | Admin selection was rejected | Passed |
| T-17 | Authentication | Seeded Admin can log in | Integration | Valid seeded credentials return a JWT | JWT returned for synthetic Admin | Passed |
| T-18 | Security | Missing or invalid JWT is rejected | Security integration | Request is rejected with 401 | Both requests returned 401 | Passed |
| T-19 | Security | Freight Owner cannot access an Admin route | Security integration | Request is rejected with 403 | Request returned 403 | Passed |
| T-20 | Audit | Registration and login create audit records | Integration | Safe audit events are stored | Registration and login events stored | Passed |

More integration tests may be added after the core end-to-end journey works.
