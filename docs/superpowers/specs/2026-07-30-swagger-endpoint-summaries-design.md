# Swagger Endpoint Summaries Design

## Goal

Show a short, useful sentence beside every API endpoint in Swagger UI so users
can understand each operation without expanding it.

## Approach

Add an OpenAPI `@Operation(summary = "...")` annotation to every mapped method
in `AuthController` and `MvpController`. Summaries will be concise,
action-oriented sentences that describe the endpoint's observable purpose.

Examples:

- `POST /api/auth/login`: “Authenticate a user and return a JWT.”
- `GET /api/users/me`: “Return the authenticated user's profile.”
- `POST /api/loads/{loadId}/matches`: “Generate eligible truck matches for a load.”

Keeping each summary beside its controller method makes the documentation easy
to find and maintain when the endpoint changes.

## Scope

All 28 existing endpoints will receive summaries:

- 2 authentication endpoints
- 3 current-user and compliance endpoints
- 4 load endpoints
- 4 truck endpoints
- 9 matching, receipt, tracking, rating, and dispute endpoints
- 6 administration endpoints

No routes, authorization rules, validation, request or response models,
business logic, or status codes will change.

## Alternatives Considered

A central OpenAPI customizer could assign summaries by HTTP method and path,
but it would separate documentation from implementation and duplicate route
information. JavaDoc comments are insufficient because Springdoc does not
reliably expose them as Swagger operation summaries.

## Verification

Add an integration test that requests `/v3/api-docs` and verifies:

- all 28 expected operations are present;
- every operation has a nonblank `summary`;
- representative endpoints expose the intended summary text.

Run the complete Maven test suite to confirm the annotation-only change does
not alter application behavior.
