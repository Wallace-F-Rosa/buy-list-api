# AGENTS.md

## Project overview
This repository contains a Spring Boot 3.2.5 REST API for managing grocery ingredients and buy lists. The application uses Java 21, Gradle, Spring Security, Spring Data JPA, and Springdoc OpenAPI.

## Important repository context
- Entry point: src/main/java/com/project/buylist/BuylistApplication.java
- Main packages:
  - com.project.buylist.buylist: buy list domain, controller, service, repository, and filters
  - com.project.buylist.ingredients: ingredient domain and endpoints
  - com.project.buylist.core.security: JWT-based authentication and security configuration
  - com.project.buylist.swagger: OpenAPI and Swagger integration
- Runtime profile files live under src/main/resources.
- Local run command: ./gradlew bootRun --args="--spring.profiles.active=local"
- API base path: /api
- Swagger UI: /docs

## Important product rule
- The authenticated user identity is derived from the JWT subject.
- Buy lists must remain scoped to that authenticated user; do not expose another user's lists.
- This project currently uses the JWT subject as the user identifier and does not persist a separate user model.

## Coding guidelines
- Prefer small, focused changes that match the existing package structure.
- Keep controllers thin; place business logic in services.
- Reuse existing repositories, specifications, and DTOs when possible.
- Follow the existing Lombok and validation patterns.
- Preserve the current JWT-based security model and avoid bypassing auth checks.
- Keep REST naming consistent with the existing /api/ingredient and /api/buylist patterns.

## Testing expectations
- Add or update tests for any behavior change.
- Prefer real API-level assertions where possible.
- Run relevant tests before finishing a change.

## Documentation and architecture maintenance
- If you change package structure, endpoints, security behavior, persistence, or runtime configuration, update the agent guidance files in the same change.
- Keep [AGENTS.md](AGENTS.md), [.github/copilot-instructions.md](.github/copilot-instructions.md), [docs/architecture.md](docs/architecture.md), and [docs/testing.md](docs/testing.md) aligned with the current implementation.
- When adding new features, update the docs to reflect the new flow, dependencies, and test expectations.

## Commands
- Run the full test suite: ./gradlew test
- Run the application locally: ./gradlew bootRun --args="--spring.profiles.active=local"
