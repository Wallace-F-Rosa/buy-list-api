# Architecture overview

## Application purpose
The BuyList API is a Spring Boot application for managing ingredients and user-owned buy lists. The system supports CRUD-style operations, authenticated access, pagination, filtering, and OpenAPI documentation.

## High-level layers
- Controllers: REST endpoints under the buylist and ingredients packages.
- Services: implement domain behavior and coordinate persistence.
- Repositories: Spring Data JPA repositories for persistence operations.
- Models: JPA entities such as Ingredient, BuyList, and BuyListItem.
- Security: JWT-based authentication and authorization handled in the core security package.
- Swagger: OpenAPI configuration exposed through the springdoc integration.

## Main packages
- com.project.buylist.buylist: buy list operations and related filters.
- com.project.buylist.ingredients: ingredient operations and related filters.
- com.project.buylist.core.security: authentication and security configuration.
- com.project.buylist.swagger: Swagger and OpenAPI configuration.

## Request flow
1. A client calls a REST endpoint under /api.
2. The controller parses request data and delegates to the corresponding service.
3. The service performs validation and business logic.
4. The repository persists or reads entities through Spring Data JPA.
5. The controller returns a response body or status code.

## Persistence and configuration
- The application uses Spring Data JPA and an embedded H2 database for local development.
- Profile-specific configuration is provided by the application-local, application-prd, and application-test files.
- Tests use the test profile and isolated test configuration.

## API surface
- Ingredient endpoints are available under /api/ingredient.
- Buy list endpoints are available under /api/buylist.
- Swagger UI is served at /docs.

## Maintenance note
When the codebase evolves, keep this document aligned with the current implementation. Any change to packages, request flow, auth behavior, persistence, or deployment profile should be reflected here.
