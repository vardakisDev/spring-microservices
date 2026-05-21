# Quality Gates

This file tracks the two explicit quality requirements from the assessment that
go beyond compilation and unit tests:

- 42Crunch OpenAPI security audit score of 100
- Sonar analysis

## Current Status

### 42Crunch

Status: in progress. Contract hardening has been applied, but the final audited
100 score still needs to be confirmed and recorded.

What is already in place:

- OpenAPI contracts exist for the root assessment contract and for each service
- Bearer authentication schemes are defined in the contracts
- explicit `401` and `403` responses are defined
- request validation and shared error schemas are present
- runtime bearer JWT enforcement now exists in cart-service, user-service, and
  product-service

What is still required:

- run the 42Crunch audit against the relevant OpenAPI files
- review every finding and adjust the contracts until the score reaches 100
- record the final audited contract and result in the delivery notes

Likely audit targets:

- `openapi/openapi.yaml`
- `services/user-service/openapi/openapi.yaml`
- `services/product-service/openapi/openapi.yaml`
- `services/cart-service/openapi/openapi.yaml`

Important note:

- the assessment wording is strongest around the root `openapi/openapi.yaml`
  file, so that file must definitely be audited
- because this repository evolved into a split microservice system, it is also
  sensible to audit the service-owned contracts

## 42Crunch Checklist

Before running the audit, confirm the contracts still satisfy these basics:

- security scheme defined
- operation-level or global security requirement defined
- explicit `401` and `403` responses present for protected endpoints
- `additionalProperties: false` used where appropriate on request and response
  schemas
- path parameters marked required and typed correctly
- request bodies typed and validated
- error responses are explicit and reusable

## Sonar

Status: executed successfully, with a passing quality gate.

What is already in place:

- the Maven build includes `org.sonarsource.scanner.maven:sonar-maven-plugin`
- a root `sonar-project.properties` file now provides default project metadata
  and excludes generated sources from analysis
- a helper script is available at `scripts/run-sonar.sh`
- the project builds and the multi-module tests pass
- a Sonar analysis has been run and the quality gate passed

What is still required:

- point the scan at a real SonarQube or SonarCloud instance
- provide a real project key and token
- rerun the analysis when new changes need to be re-evaluated
- decide whether to add project-specific Sonar rules, exclusions, or quality
  gate documentation

Suggested command:

`mvn clean verify sonar:sonar -Dsonar.projectKey=<your-project-key> -Dsonar.host.url=<your-sonar-url> -Dsonar.token=<your-token>`

Helper script:

`./scripts/run-sonar.sh`

If using SonarCloud instead of self-hosted SonarQube, also provide the
organization when needed.

## Delivery Expectation

Before considering the project assessment-ready, the following should be true:

1. The root OpenAPI contract has a 42Crunch score of 100.
2. Sonar analysis has been executed successfully.
3. Any non-trivial findings from either tool have been resolved or explicitly
   justified.