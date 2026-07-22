# Testing guide

## Test strategy
The repository uses a mix of service-focused tests and Spring Boot integration tests.

## Where tests live
- src/test/java/com/project/buylist/buylist: buy list controller and service tests
- src/test/java/com/project/buylist/ingredients: ingredient-related integration tests
- src/test/resources/application-test.yml: test profile configuration

## Test conventions
- Integration tests use Spring Boot test support and MockMvc for API-level validation.
- Security-aware tests use Spring Security test support and JWT-based authentication mocks.
- Tests are intended to run against the test profile to keep them isolated from local development data.

## Running tests
- Full suite: ./gradlew test
- Targeted controller tests: ./gradlew test --tests "*BuyListControllerIntegrationTest"
- Targeted service tests: ./gradlew test --tests "*BuyListServiceTest"

## Guidelines for new tests
- Cover both success and failure cases, including unauthorized access.
- Prefer asserting on real API behavior through MockMvc rather than only checking mocked collaborators.
- Keep fixtures small and explicit.
