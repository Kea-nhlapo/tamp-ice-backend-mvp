# Testing Summary

The following tests are planned. Actual results will be recorded only after implementation and execution.

## Scaffold checks

| Test ID | Area | Test description | Test type | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| S-01 | Application | Spring application context loads | Integration | Application starts for the test | Application context loaded | Passed |
| S-02 | Health | Health endpoint returns HTTP 200 | Integration | Health request succeeds | HTTP 200 returned | Passed |

## Planned business tests

| Test ID | Area | Test description | Test type | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| T-01 | Authentication | Registration succeeds | Integration | User is created and a successful response is returned | Not available | Not run |
| T-02 | Authentication | Duplicate email fails | Integration | Conflict response; no duplicate user | Not available | Not run |
| T-03 | Security | Password is not stored as plain text | Unit/integration | Stored hash differs from submitted password and BCrypt verifies it | Not available | Not run |
| T-04 | Loads | Freight Owner can create a load | Integration | Valid load is persisted for its owner | Not available | Not run |
| T-05 | Loads | Transporter cannot create a load | Security integration | Request is rejected with 403 | Not available | Not run |
| T-06 | Loads | Invalid load weight is rejected | Validation integration | Request is rejected with 400 | Not available | Not run |
| T-07 | Trucks | Transporter can create a truck | Integration | Valid truck is persisted for its transporter | Not available | Not run |
| T-08 | Trucks | Freight Owner cannot create a truck | Security integration | Request is rejected with 403 | Not available | Not run |
| T-09 | Matching | Truck with insufficient capacity is rejected | Unit | Truck is ineligible with a capacity reason | Not available | Not run |
| T-10 | Matching | Incompatible truck type is rejected | Unit | Truck is ineligible with a compatibility reason | Not available | Not run |
| T-11 | Matching | Non-overlapping availability is rejected | Unit | Truck is ineligible with an availability reason | Not available | Not run |
| T-12 | Matching | Wrong location is rejected | Unit | Truck is ineligible with a location reason | Not available | Not run |
| T-13 | Matching | Valid truck produces a match with reasons | Unit | Eligible match includes a score and readable reasons | Not available | Not run |
| T-14 | Acceptance | Accepting a match creates a receipt and audit event | Integration | Match, receipt and audit event are saved atomically | Not available | Not run |
| T-15 | Administration | Non-admin user cannot access Admin metrics | Security integration | Request is rejected with 403 | Not available | Not run |

More integration tests may be added after the core end-to-end journey works.
