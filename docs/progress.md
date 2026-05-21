# Progress Log

## Current Status

- Core implementation phase is active.
- `cart-service` domain model and application use cases are implemented and
  tested.
- `user-service` and `product-service` are scaffolded as working vertical
  slices with temporary in-memory adapters.
- `cart-service` now has synchronous HTTP client adapters for `user-service`
  and `product-service`.
- `cart-service` now exposes its generated OpenAPI contract through concrete
  controller adapters.
- `cart-service` now persists carts and purchases through PostgreSQL-backed JPA
  adapters and Flyway migrations.
- `user-service` and `product-service` now also persist through PostgreSQL-
  backed JPA adapters and Flyway migrations.
- Module-level validation currently passes for `cart-service`, `user-service`,
  and `product-service`.
- Sonar plugin setup exists in the Maven build, but Sonar analysis has not yet
  been run against a live server.
- 42Crunch audit has not yet been run in this repository session.

## Completed Steps

1. Read and translated the assessment into an implementation plan.
2. Bootstrapped a Spring Boot Maven project with OpenAPI generation support.
3. Added an initial OpenAPI contract and validated generation.
4. Split the contract into:
   - root `cart-service` contract
   - `user-service` contract
   - `product-service` contract
   - `cart-service` service-owned contract
5. Fixed the remaining cart-contract parameter issue and revalidated generation.
6. Implemented `cart-service` domain rules and application ports/use cases.
7. Added focused unit tests for pricing, cart behavior, cart creation, and top
  purchased products.
8. Scaffolded `user-service` with generated API implementation, use cases, and
  in-memory persistence.
9. Scaffolded `product-service` with generated API implementation, use cases,
  and in-memory persistence.
10. Wired synchronous HTTP adapters in `cart-service` for user and product
   lookups.
11. Implemented the `cart-service` inbound HTTP layer for carts, checkout, and
  purchase-history queries.
12. Replaced temporary in-memory cart and purchase repositories with
    PostgreSQL-backed JPA adapters and a Flyway schema.
13. Replaced temporary in-memory user and product repositories with
    PostgreSQL-backed JPA adapters and Flyway schemas.
14. Documented Sonar and 42Crunch quality-gate requirements and remaining
  execution steps.

## Current Repository Shape

- `openapi/openapi.yaml` is the root cart-service contract.
- `services/user-service/openapi/openapi.yaml` exists.
- `services/product-service/openapi/openapi.yaml` exists.
- `services/cart-service/openapi/openapi.yaml` exists.
- The Maven multi-module build is scaffolded.
- `services/user-service` is executable with PostgreSQL-backed persistence.
- `services/product-service` is executable with PostgreSQL-backed persistence.
- `services/cart-service` has tested domain/application logic, generated HTTP
  endpoints, synchronous upstream HTTP clients, and PostgreSQL-backed
  persistence for carts and purchases.

## Validation Performed

- `mvn -DskipTests generate-sources` from the repository root: passed.
- `mvn -q -pl services/cart-service test`: passed.
- `mvn -q -pl services/user-service test`: passed.
- `mvn -q -pl services/product-service test`: passed.

## Next Step

Run the remaining external quality gates and live-runtime validation:

- execute Sonar against a real server
- run 42Crunch audit until the OpenAPI contract reaches 100
- validate the services against live PostgreSQL databases

## Known Risks

- The chosen architecture exceeds the minimum brief.
- The more services we add, the more setup, testing, and orchestration work we
  must finish.
- `cart-service` must remain complete enough to satisfy the original assignment
  even if the full platform is not finished.
- The current synchronous HTTP wiring assumes upstream services are available;
  resilience concerns such as retries, timeouts, and fallback policies are not
  handled yet.
- The PostgreSQL persistence slices have compile and unit-test validation, but
  they have not yet been exercised against a live PostgreSQL instance in this
  session.
- The required 42Crunch score has not yet been proven in this session.
- Sonar has not yet been executed against a real SonarQube or SonarCloud
  target.