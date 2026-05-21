# Shopping Platform Interview Project

This repository is being developed as a production-style shopping platform with a
microservice split, while still preserving the original assessment artifact at
`openapi/openapi.yaml` for the cart-service.

## Current Direction

- `cart-service` is the primary assessment anchor.
- `user-service` owns user CRUD and VIP eligibility.
- `product-service` owns product CRUD and pricing catalog data.
- The platform is being documented step by step so the architectural choices stay
	explicit and reviewable.

## Contracts

- Root assessment contract: `openapi/openapi.yaml`
- User service contract: `services/user-service/openapi/openapi.yaml`
- Product service contract: `services/product-service/openapi/openapi.yaml`
- Cart service contract: `services/cart-service/openapi/openapi.yaml`

## Documentation

- Plan: `docs/plan.md`
- Progress log: `docs/progress.md`
- Architecture notes: `docs/architecture.md`
- Decision log: `docs/decisions.md`
- Quality gates: `docs/quality-gates.md`

## Current Validation

- Root cart-service OpenAPI generation passes with `mvn -DskipTests generate-sources`.
- Full multi-module test suite passes with `mvn -q test`.

## Quality Gates

This repository has started the structural setup for Sonar by including the
Maven Sonar plugin in the build, but Sonar still needs to be executed against a
real SonarQube or SonarCloud target.

Example command:

`mvn clean verify sonar:sonar -Dsonar.projectKey=<your-project-key> -Dsonar.host.url=<your-sonar-url> -Dsonar.token=<your-token>`

Helper script:

`./scripts/run-sonar.sh`

The OpenAPI contracts also still need to be audited with 42Crunch to confirm a
score of 100, which is a hard requirement in the assessment.

## Next Step

Run live quality and runtime validation:

- execute Sonar analysis against a real Sonar server
- audit the OpenAPI contracts in 42Crunch and fix any findings until the score
	reaches 100
- run the platform against live PostgreSQL databases
