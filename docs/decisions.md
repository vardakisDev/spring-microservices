# Decision Log

## 1. Build Style

Decision: Use Maven.

Reason:

- It keeps Spring Boot and OpenAPI generation straightforward.
- It fits well with a future parent or aggregator build.

## 2. Architecture Style

Decision: Use Hexagonal Architecture inside each service.

Reason:

- Business rules stay testable and framework-independent.
- HTTP, persistence, and security stay at the edges.

## 3. API Strategy

Decision: Use OpenAPI-first development.

Reason:

- The brief explicitly requires the API contract to be the source of truth.
- Generated code should define HTTP interfaces and transport models.

## 4. Security Strategy

Decision: Use Bearer JWT security definitions in every service contract.

Reason:

- The assessment explicitly requires proper security definitions in OpenAPI.
- This is the smallest credible production-oriented contract design.

What this means in practice:

- `components.securitySchemes.BearerAuth`
- contract-level `security`
- explicit `401` and `403` responses
- explicit request validation and error schemas

Execution note:

- This strategy is implemented in the contracts, but the formal 42Crunch audit
  still needs to be executed and recorded.

## 5. Service Split

Decision: Split the platform into `user-service`, `product-service`, and
`cart-service`.

Reason:

- It demonstrates production-style bounded contexts.
- It separates ownership of users, catalog, and cart/checkout workflows.

## 6. Assessment Safety Rule

Decision: Keep `cart-service` as the primary assessment anchor.

Reason:

- The original brief is still the hard requirement.
- The extra services are an intentional extension, not a replacement.

## 7. VIP Rule

Decision: User VIP eligibility is owned by `user-service` via a boolean flag.

Reason:

- It is simple, explicit, and easy to validate from `cart-service`.

## 8. Consistency Model

Decision: No full saga implementation in the first version.

Reason:

- The first implementation can keep checkout transactional inside
  `cart-service`.
- `cart-service` can validate upstream data synchronously and snapshot what it
  needs at checkout.

Future option:

- If future workflows require writes across multiple services, add an outbox
  pattern and a saga orchestration or choreography model.

## 9. Service Wiring Strategy

Decision: Keep synchronous cross-service communication behind outbound ports in
`cart-service`.

Reason:

- The application layer already depends on `UserDirectoryPort` and
  `ProductCatalogPort`.
- Replacing stubbed implementations with HTTP adapters lets us move toward a
  real microservice topology without changing use-case code.

What this means in practice:

- `cart-service` owns the business workflow.
- infrastructure adapters use Spring `RestClient` to call `user-service` and
  `product-service`.
- only the infrastructure layer knows base URLs and transport payload shapes.

## 10. Temporary Runtime Strategy

Decision: Use in-memory repository adapters in all three services until the HTTP
and use-case slices are stable.

Reason:

- It keeps the implementation step-by-step.
- It avoids mixing persistence concerns with service-boundary validation.
- It gives us a runnable system shape before Flyway and JPA are introduced.

Tradeoff:

- Service state is not durable yet.
- The current runtime shape is ideal for interviews and slice testing, but not
  for production.

Update:

- `cart-service` has now moved off this temporary approach for cart and
  purchase persistence.
- `user-service` and `product-service` have also moved off the temporary
  in-memory model.

## 11. Use-Case Modeling Style

Decision: Use a lightweight CQRS style inside the Hexagonal Architecture.

Reason:

- It makes state-changing workflows and read-only queries explicit.
- It improves interview clarity without introducing distributed complexity.
- It fits naturally with separate use-case classes.

What this means in practice:

- commands include create cart, add item, remove item, and checkout
- queries include get cart and get top purchased products
- there is no separate read database, event sourcing, or broker-based CQRS in
  this version

## 12. Cart Persistence Strategy

Decision: Persist carts and purchases in PostgreSQL through Spring Data JPA
adapters with Flyway-managed schema.

Reason:

- The assessment explicitly requires PostgreSQL persistence.
- `cart-service` is the primary assessment anchor, so it should reach durable
  persistence first.
- Keeping JPA entities in infrastructure preserves the hexagonal boundary while
  allowing a real database-backed implementation.

What this means in practice:

- JPA entities and repositories live in infrastructure
- Flyway creates the `carts`, `cart_items`, `purchases`, and `purchase_items`
  tables
- repository adapters map between persistence entities and handwritten domain
  objects
- the core domain model remains independent from JPA annotations

## 13. Supporting Service Persistence Strategy

Decision: Persist `user-service` and `product-service` through the same
PostgreSQL plus JPA plus Flyway pattern used in `cart-service`.

Reason:

- The assessment requires PostgreSQL persistence, not just a partially durable
  platform.
- Reusing the same persistence shape across services keeps the architecture
  consistent and easier to defend.
- It preserves the same hexagonal rule: repositories are ports, JPA adapters
  are infrastructure.

What this means in practice:

- each service owns its own Flyway migrations and tables
- each service keeps its domain model free from JPA annotations
- each repository adapter maps between persistence entities and domain objects