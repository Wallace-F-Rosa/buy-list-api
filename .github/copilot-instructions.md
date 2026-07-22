# GitHub Copilot instructions for this repository

## Repository context
This is a Spring Boot REST API for managing grocery ingredients and buy lists. The project uses Java 21, Gradle, Spring Boot 3.2.5, Spring Security, Spring Data JPA, and Springdoc OpenAPI.

## Code conventions
- Keep Java classes organized by domain package under src/main/java/com/project/buylist.
- Favor the existing layered structure: controller -> service -> repository.
- Use validation annotations and existing DTO/filter classes for request handling.
- Preserve the current authentication approach based on JWT claims and the authenticated user subject.
- Follow the project's current naming style for controllers, services, repositories, and entities.

## Development workflow
- Prefer incremental changes and keep them scoped to the relevant feature area.
- When changing behavior, update or add tests under src/test/java.
- Verify with ./gradlew test before concluding work.
- Use the local profile for runtime checks: ./gradlew bootRun --args="--spring.profiles.active=local"

## Notes
- The API exposes endpoints under /api and Swagger UI under /docs.
- Existing tests use Spring Boot integration support and profile-specific configuration.
- User visibility is enforced by the JWT subject; do not make cross-user data visible.
