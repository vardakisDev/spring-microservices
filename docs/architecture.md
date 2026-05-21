# Architecture Notes

## System Shape

The repository is being shaped as a small microservice platform.

Planned services:

- `user-service`
- `product-service`
- `cart-service`

This is larger than the minimum brief, so the architecture must stay disciplined
and reviewer-friendly.

## Service Responsibilities

### user-service

Owns:

- user CRUD
- VIP eligibility

Does not own:

- carts
- product catalog
- purchase history

### product-service

Owns:

- product CRUD
- current catalog price

Does not own:

- carts
- users
- checkout history

### cart-service

Owns:

- cart creation, retrieval, and deletion
- add/remove product from cart
- pricing rules
- checkout
- purchase history
- top four expensive purchased products query

This is the most important service because it directly maps to the original
assessment.

It now also contains synchronous read adapters for:

- user lookup from `user-service`
- product lookup from `product-service`

Those adapters live in infrastructure, not in the application layer.

## Hexagonal Architecture

Each service should use three main layers.

### Domain

Contains pure business concepts and rules.

Examples:

- `Cart`
- `CartItem`
- `PricingPolicy`
- `User`
- `Product`

Rules:

- no Spring annotations
- no JPA entities
- no HTTP classes

### Application

Contains use cases and ports.

Examples:

- create cart
- add item to cart
- checkout cart
- create user
- create product

Rules:

- orchestrates domain logic
- depends on ports, not implementations

### Infrastructure

Contains technical adapters.

Examples:

- generated API implementations
- Spring MVC controllers or delegates
- JPA repositories and entities
- REST clients to other services
- Spring Security configuration

Current repository direction:

- `user-service` and `product-service` expose generated HTTP interfaces through
	controller adapters.
- `cart-service` currently consumes those services through Spring `RestClient`
	adapters that implement outbound ports.
- `cart-service` also exposes its own generated cart and purchase-history
	interfaces through controller adapters that delegate into use cases.
- `cart-service` persists carts and purchases through Spring Data JPA adapters
	backed by PostgreSQL tables managed with Flyway.
- `user-service` persists user data through Spring Data JPA adapters backed by
	PostgreSQL and Flyway.
- `product-service` persists product catalog data through Spring Data JPA
	adapters backed by PostgreSQL and Flyway.

## OpenAPI-First

The source of truth is the contract, not handwritten controller code.

Production-style contract ownership in this repository:

- root `openapi/openapi.yaml` is the assessment-facing cart contract
- each service also owns its own OpenAPI file

Why this matters:

- service boundaries stay explicit
- generated code remains consistent with the contract
- reviewers can inspect the API before reading the implementation

Important boundary rule:

- generated request and response classes stay at the edge
- domain models remain handwritten inside each service
- outbound adapters map transport payloads into application-facing snapshots

## Security Definitions in OpenAPI

The brief explicitly requires security definitions in the contract.

That means each service contract should include:

- a `BearerAuth` security scheme
- protected operations
- `401 Unauthorized` response definitions
- `403 Forbidden` response definitions
- explicit request and error schemas

Important distinction:

- OpenAPI defines the contract of security
- Spring Security enforces that contract at runtime

Both are required for a credible implementation.

## Saga Pattern

Saga is a distributed consistency pattern used when a workflow spans multiple
services and no single local database transaction can safely own the whole flow.

Why it is not the first implementation target here:

- checkout can be kept as a local transaction inside `cart-service`
- upstream user and product data can be read synchronously
- purchase history can snapshot what it needs at checkout time

When saga would become relevant:

- if checkout needed to reserve stock in `product-service`
- if payment became a separate service
- if shipping or notifications needed coordinated writes

At that point, the next production step would be:

- outbox pattern
- domain events
- orchestration or choreography for the distributed workflow

## Production-Like, But Controlled

This project aims to look more production-like without adding complexity that is
irrelevant to the brief.

Included direction:

- service boundaries
- API-first contracts
- explicit security contracts
- PostgreSQL persistence
- hexagonal internals

Current persistence status:

- all three services now use real PostgreSQL-backed adapters
- cart items and purchase items are persisted in separate collection tables
- the domain aggregate is rehydrated from persistence through mapping adapters,
	not by exposing JPA entities to the core
- user and product records are persisted through dedicated service-local tables
	with service-owned Flyway migrations

Excluded for now:

- Kubernetes
- service mesh
- complex gateway platform
- full distributed saga implementation

## Current Implementation Phase

The project is intentionally being built in vertical slices.

Current slice order:

1. split and validate OpenAPI contracts
2. implement `cart-service` domain and use cases
3. scaffold `user-service` and `product-service`
4. wire synchronous reads from `cart-service` to the other services
5. expose `cart-service` through its generated HTTP contract
6. replace temporary repository adapters with PostgreSQL persistence
7. move remaining services from in-memory adapters to PostgreSQL
8. validate the whole platform against a live multi-service runtime

Why this order is useful:

- the core pricing and checkout rules are proven first
- supporting services are kept minimal but realistic
- infrastructure grows around stable application ports instead of leaking into
	the core too early