# Planned API Contract

This is a planning-level contract. Payload schemas, error fields and OpenAPI annotations will be finalised during implementation.

## Authentication

**Required role:** Public.

**Purpose:** Register an allowed user role and exchange valid credentials for a JWT.

**Expected main response:** Registration returns the created user's safe profile; login returns a token and basic identity information.

**Important validation:** Email must be valid and unique, passwords must meet the implemented policy, and only supported roles may be selected. Passwords must never appear in responses.

| Method | Endpoint | Planned use |
|---|---|---|
| POST | `/api/auth/register` | Register a Freight Owner, Transporter or approved Admin flow |
| POST | `/api/auth/login` | Authenticate and obtain a JWT |

## Current user and compliance

**Required role:** Any authenticated user for their own profile and submissions.

**Purpose:** Read or update the authenticated user's identity and submit mock compliance-document metadata.

**Expected main response:** A safe profile or the stored compliance metadata with its review status.

**Important validation:** Users may act only on their own profile; document type and name or reference are required. Uploading real documents is outside scope.

| Method | Endpoint | Planned use |
|---|---|---|
| GET | `/api/users/me` | Read the current profile |
| PUT | `/api/users/me` | Update allowed profile fields |
| POST | `/api/users/me/compliance-documents` | Submit mock document metadata |

## Loads

**Required role:** Freight Owner for creation and updates; authenticated access for permitted reads.

**Purpose:** Manage cargo load postings.

**Expected main response:** A load representation containing route, cargo, measurements, pickup window and status.

**Important validation:** Origin, destination and cargo type are required; weight and volume must be positive; pickup start must precede pickup end; update requires ownership.

| Method | Endpoint | Planned use |
|---|---|---|
| POST | `/api/loads` | Create a load |
| GET | `/api/loads` | List visible loads |
| GET | `/api/loads/{id}` | View one load |
| PUT | `/api/loads/{id}` | Update an owned load |

## Trucks

**Required role:** Transporter for creation and updates; authenticated access for permitted reads.

**Purpose:** Manage available truck postings.

**Expected main response:** A truck representation containing type, capacity, location, availability window and status.

**Important validation:** Truck type and location are required; capacity must be positive; availability start must precede availability end; update requires ownership.

| Method | Endpoint | Planned use |
|---|---|---|
| POST | `/api/trucks` | Create a truck |
| GET | `/api/trucks` | List visible trucks |
| GET | `/api/trucks/{id}` | View one truck |
| PUT | `/api/trucks/{id}` | Update an owned truck |

## Matching

**Required role:** Freight Owner who owns the load; related authenticated parties may view a match.

**Purpose:** Compare available trucks with a load and return explainable eligible matches.

**Expected main response:** Match candidates containing truck identity, score, reasons and status.

**Important validation:** Reject candidates with insufficient capacity, incompatible truck and cargo types, non-overlapping availability or a location that does not match the origin city or area.

| Method | Endpoint | Planned use |
|---|---|---|
| POST | `/api/loads/{loadId}/matches` | Generate match candidates |
| GET | `/api/matches/{id}` | View an authorised match |

## Acceptance and receipt

**Required role:** An authenticated party authorised for the match.

**Purpose:** Record a match decision and retrieve the acceptance receipt.

**Expected main response:** The changed match status; acceptance also makes a receipt available with contract ID, actor, timestamp, IP address and user-agent where available.

**Important validation:** Only allowed state transitions may occur; repeated or conflicting decisions must be rejected; a receipt exists only for an accepted match.

| Method | Endpoint | Planned use |
|---|---|---|
| POST | `/api/matches/{id}/accept` | Accept a match |
| POST | `/api/matches/{id}/reject` | Reject a match |
| GET | `/api/matches/{id}/receipt` | Retrieve an acceptance receipt |

## Tracking

**Required role:** A party to an accepted match; permitted Administrators may read for oversight.

**Purpose:** Append and retrieve simulated trip progress.

**Expected main response:** An ordered collection of status or mock-coordinate events with timestamps.

**Important validation:** The match must be accepted, the actor must be authorised and status movement must follow the implemented progression.

| Method | Endpoint | Planned use |
|---|---|---|
| POST | `/api/matches/{id}/tracking-events` | Add the next mock event |
| GET | `/api/matches/{id}/tracking-events` | List trip events |

## Ratings and disputes

**Required role:** An authenticated party to the match.

**Purpose:** Rate the other party after completion or open a basic dispute.

**Expected main response:** The stored rating or dispute with its status and timestamp.

**Important validation:** Ratings are allowed only after completion, must target the other party and use a score from 1 to 5. Dispute reasons are required and access is limited to relevant parties or Administrators.

| Method | Endpoint | Planned use |
|---|---|---|
| POST | `/api/matches/{id}/ratings` | Submit a post-completion rating |
| POST | `/api/matches/{id}/disputes` | Create a dispute |

## Administration

**Required role:** Administrator.

**Purpose:** Review users, decide compliance status, manage disputes, inspect audit events and view basic counts.

**Expected main response:** Paged administrative records or aggregate counts for users, loads, trucks, matches, accepted matches, completed trips and open disputes.

**Important validation:** Every endpoint must reject non-admin users. Status changes must use allowed values and create audit evidence.

| Method | Endpoint | Planned use |
|---|---|---|
| GET | `/api/admin/users` | List users |
| PATCH | `/api/admin/users/{id}/compliance-status` | Approve or reject compliance |
| GET | `/api/admin/disputes` | List disputes |
| PATCH | `/api/admin/disputes/{id}/status` | Change dispute status |
| GET | `/api/admin/audit-events` | List audit events |
| GET | `/api/admin/metrics` | Read simple aggregate metrics |
