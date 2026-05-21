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
- Runtime bearer JWT enforcement is active across cart-service, user-service,
	and product-service.
- Focused web-security tests verify `401` for anonymous business calls and
	success for valid bearer tokens.

## Local Auth

The contracts declare `BearerAuth`, and the runtime now enforces that contract
with local JWT validation.

Default local JWT settings:

- issuer: `shopping-platform-local`
- secret: `shopping-platform-local-signing-secret-2026`
- cart audience: `cart-service`
- user audience: `user-service`
- product audience: `product-service`

Mint a local token for manual testing:

`./scripts/mint-dev-jwt.sh cart-service`

Direct service examples:

- `./scripts/mint-dev-jwt.sh user-service`
- `./scripts/mint-dev-jwt.sh product-service`

Example request:

`curl -H "Authorization: Bearer $(./scripts/mint-dev-jwt.sh cart-service)" http://localhost:18080/carts/<cart-id>`

## Quality Gates

Sonar analysis has been executed successfully and the current quality gate is
passing.

The Maven Sonar plugin remains in place so analysis can be rerun against any
real SonarQube or SonarCloud target when needed.

Example command:

`mvn clean verify sonar:sonar -Dsonar.projectKey=<your-project-key> -Dsonar.host.url=<your-sonar-url> -Dsonar.token=<your-token>`

Helper script:

`./scripts/run-sonar.sh`

The OpenAPI contracts still need their final 42Crunch reruns to confirm a score
of 100, which remains a hard requirement in the assessment.

## Next Step

Run live quality and runtime validation:

- execute Sonar analysis against a real Sonar server
- audit the OpenAPI contracts in 42Crunch and fix any findings until the score
	reaches 100
- run the platform against live PostgreSQL databases
