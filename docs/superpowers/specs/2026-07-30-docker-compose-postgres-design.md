# Docker Compose and PostgreSQL Design

## Goal

Package the TAMP Spring Boot backend as a reproducible container and provide a
Docker Compose setup that runs it with PostgreSQL. The resulting local
environment must expose Swagger UI on port 8080 and preserve database data
across container restarts.

## Architecture

The environment contains two Compose services on the default private network:

- `app` builds and runs the Spring Boot backend.
- `postgres` runs PostgreSQL and stores its database files in a named volume.

The application uses the existing `postgres` Spring profile. It reaches the
database through the Compose service name rather than a host-bound database
address. Only the application port is required for normal use.

## Container Build

The application image uses a multi-stage Dockerfile:

1. A Java 21 build stage copies the Maven wrapper and project sources, resolves
   dependencies, runs the automated tests, and packages the executable JAR.
2. A smaller Java 21 runtime stage copies only the packaged JAR and runs it as a
   non-root user.

The Docker build context excludes Git metadata, build output, IDE files, local
environment files, and other files that are not needed to build the service.

## Configuration and Secrets

Compose supplies:

- `SPRING_PROFILES_ACTIVE=postgres`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`

Database credentials and the JWT secret come from a local `.env` file. The
tracked `.env.example` documents safe development placeholders and the expected
Base64 JWT secret format. Real secrets are not embedded in the image or
committed to Git.

## Startup and Health

PostgreSQL has a `pg_isready` health check. The application depends on the
database reaching the healthy state before it starts. The application has an
HTTP health check against `/actuator/health`.

Flyway remains responsible for schema creation and synthetic seed data. The
PostgreSQL named volume preserves state between ordinary Compose stops and
starts. Removing the volume intentionally resets the database.

## User Workflow

The README documents:

1. Copying `.env.example` to `.env`.
2. Replacing the JWT placeholder with a Base64-encoded 32-byte secret.
3. Starting the stack with `docker compose up --build`.
4. Opening Swagger UI at
   `http://localhost:8080/swagger-ui/index.html`.
5. Stopping the stack without deleting its data.

The existing non-Docker H2 workflow remains supported.

## Failure Handling

- The database health check prevents the application from racing PostgreSQL
  startup.
- Spring Boot exits visibly if required environment variables are absent or
  invalid.
- Compose restart policies recover from unexpected service failures without
  hiding configuration errors behind infinite startup loops.
- Container logs remain available through `docker compose logs`.

## Verification

Implementation is complete when:

- The existing automated tests pass.
- The application image builds successfully.
- `docker compose config` validates the configuration.
- Both services become healthy.
- `/actuator/health` returns HTTP 200 with an `UP` status.
- Swagger UI and `/v3/api-docs` return HTTP 200.
- A seeded user can log in and use the returned JWT on
  `GET /api/users/me`.
- Restarting the stack without removing volumes retains PostgreSQL data.

