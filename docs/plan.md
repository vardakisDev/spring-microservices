# Implementation Plan

## Goal

Build a production-style shopping platform in a single repository using three
Spring Boot microservices:

- `user-service`
- `product-service`
- `cart-service`

The original assessment only requires one Spring Boot microservice, but this
implementation intentionally expands the scope to demonstrate production-style
service boundaries. Because of that, `cart-service` remains the primary
assessment anchor and must always be deliverable even if the surrounding
platform stays partial.

## Why This Plan Exists

The project is being built step by step for learning, not just delivery. Each
phase should leave behind both code and documentation so the reasoning is clear.

## Planned Phases

### Phase 0: Documentation and Scope Control

1. Document the architectural direction and the risks of going beyond the brief.
2. Keep the original assessment requirement visible and traceable.
3. Define service ownership and contract ownership before more code is added.

### Phase 1: Contract-First Service Boundaries

1. Maintain the root `openapi/openapi.yaml` as the assessment-facing contract for
   `cart-service`.
2. Define service-owned contracts for:
   - `services/user-service/openapi/openapi.yaml`
   - `services/product-service/openapi/openapi.yaml`
   - `services/cart-service/openapi/openapi.yaml`
3. Ensure security definitions are explicit in each service contract:
   - `BearerAuth`
   - `401` responses
   - `403` responses
   - constrained request and response schemas
   - stable error payloads

### Phase 2: Multi-Service Build Structure

1. Introduce a parent Maven build that compiles all services together.
2. Move service-specific code under:
   - `services/user-service`
   - `services/product-service`
   - `services/cart-service`
3. Keep shared documentation and orchestration at the repository root.

### Phase 3: Service Scaffolding

1. Scaffold `user-service` from its OpenAPI contract.
2. Scaffold `product-service` from its OpenAPI contract.
3. Reshape the current root bootstrap into `cart-service`.

### Phase 4: Hexagonal Implementation

Implement each service using:

- `domain`
- `application`
- `infrastructure`

Service responsibilities:

- `user-service`: user CRUD and VIP eligibility.
- `product-service`: product CRUD and current product pricing.
- `cart-service`: cart lifecycle, pricing rules, checkout, and purchase history.

### Phase 5: Security and Communication

1. Add runtime Bearer JWT enforcement to each service.
2. Use synchronous REST calls from `cart-service` to `user-service` and
   `product-service` for lookup and validation.
3. Snapshot relevant user/product data inside `cart-service` at checkout so
   purchase history remains historically correct.

### Phase 6: Data and Testing

1. Add PostgreSQL persistence per service.
2. Add Flyway migrations per service.
3. Add unit tests for domain rules.
4. Add focused integration tests against PostgreSQL.
5. Add end-to-end flow checks across the three services.

## Delivery Rule

If scope becomes a risk, prioritize in this order:

1. `cart-service`
2. `user-service`
3. `product-service`

That ordering protects the original assessment outcome.