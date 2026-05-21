# Hexagonal Architecture Interview Questions

This file collects likely interview questions about Hexagonal Architecture and
how it is applied in this project.

## 1. Why did you choose Hexagonal Architecture for this project?

Suggested answer:

I chose Hexagonal Architecture because the core value of this assessment is the
business logic, especially the cart rules, pricing rules, checkout behavior,
and purchase-history behavior. Hexagonal Architecture keeps that business logic
independent from HTTP, persistence, and framework details, so it is easier to
test and easier to evolve.

Short version:

- it keeps business rules at the center
- it prevents framework code from leaking into the core
- it makes unit testing much easier

## 2. What does Hexagonal Architecture mean in simple terms?

Suggested answer:

It means the application core should not depend directly on external delivery
or infrastructure mechanisms. The domain and use cases live in the center, and
they communicate through abstractions called ports. The outside world connects
through adapters that implement those ports.

Simple explanation:

- core logic in the middle
- ports define boundaries
- adapters connect external technology to the core

## 3. What are the layers in your implementation?

Suggested answer:

In this project I treat the structure in three parts:

- domain: pure business concepts and rules
- application: use cases and ports
- infrastructure: HTTP adapters, persistence adapters, clients, and security

The important rule is that the inner layers do not depend on the outer layers.

## 4. What is a port in Hexagonal Architecture?

Suggested answer:

A port is an interface owned by the application core that defines how the core
communicates across a boundary. It represents a business dependency, not a
technical implementation detail.

In this project, a port like `UserDirectoryPort` means that `cart-service`
needs user information, but the use case should not know whether that data
comes from HTTP, a database, or a test fake.

## 5. Is `UserDirectoryPort` an example of Hexagonal Architecture?

Suggested answer:

Yes. `UserDirectoryPort` is an outbound port. The cart application layer uses
it to request user information from outside the service core. The use case only
depends on the abstraction, while an infrastructure adapter will later provide
the real implementation.

Project example:

- `CreateCartUseCase` needs user information
- it depends on `UserDirectoryPort`
- later an HTTP adapter can implement that port and call `user-service`

## 6. Why not call the REST client directly from the use case?

Suggested answer:

Because that would couple the use case to infrastructure details. If the use
case knows about HTTP clients, URLs, JSON, or framework code, the core stops
being independent. By depending on a port, the use case only expresses the
business need and stays testable in isolation.

## 7. What is an outbound port?

Suggested answer:

An outbound port is used when the application core needs something from the
outside world. In this project, examples include:

- `UserDirectoryPort`
- `ProductCatalogPort`
- `CartRepositoryPort`
- `PurchaseRepositoryPort`

The request starts inside the application core and goes out through the port.

## 8. What is an inbound port?

Suggested answer:

An inbound port is how the outside world drives the application core. In many
Hexagonal or Clean Architecture interpretations, the use cases themselves are
treated as inbound ports because controllers or message listeners call them to
trigger business behavior.

Examples in this project:

- `CreateCartUseCase`
- `AddCartItemUseCase`
- `CheckoutCartUseCase`

## 9. What is an adapter?

Suggested answer:

An adapter is the technical implementation that connects a port to a real
technology. The core owns the port, and infrastructure owns the adapter.

Examples for this project:

- an HTTP client that implements `UserDirectoryPort`
- a JPA repository adapter that implements `CartRepositoryPort`
- a controller or generated API implementation that calls `CreateCartUseCase`

## 10. How does Hexagonal Architecture help testing?

Suggested answer:

It makes testing easier because the core depends on interfaces instead of real
infrastructure. In tests, I can replace adapters with small in-memory fakes or
stubs. That means I can test business rules without running databases or other
services.

Project example:

The cart-service tests use simple fake implementations of ports instead of real
HTTP calls or real repositories.

## 11. Did Hexagonal Architecture affect the domain model design?

Suggested answer:

Yes. It encouraged me to keep the domain model pure and focused on business
rules rather than API or database shape. That is why the domain `Cart` and the
generated OpenAPI transport models are treated as separate concerns.

## 12. How does Hexagonal Architecture fit with OpenAPI-first design?

Suggested answer:

They fit well together because they solve different problems at different
levels. OpenAPI defines the external HTTP contract at the edge of the system.
Hexagonal Architecture defines how that edge is separated from the application
and domain core.

Simple explanation:

- OpenAPI defines the transport boundary
- Hexagonal Architecture protects the business core behind that boundary

## 13. What is the most important benefit you got from using Hexagonal Architecture here?

Suggested answer:

The biggest benefit was keeping the pricing and checkout rules independent from
framework details. That made the most important part of the project easier to
reason about, easier to test, and easier to evolve.

## 14. Is Hexagonal Architecture always worth using?

Suggested answer:

Not always in the same level of strictness. For very small or throwaway
projects, it can be more structure than necessary. But for a service with real
business rules and integration boundaries, it gives a strong separation of
concerns and makes the design easier to defend.

## 15. Strong explanation of ports and adapters

Suggested answer:

A good way to think about Hexagonal Architecture is that the core owns the
interfaces and the outside world owns the implementations. A port is an
abstraction defined by the application core, and an adapter is the technical
implementation that plugs into that abstraction.

In this project, `UserDirectoryPort` is a good example. The cart use case needs
user information, but it should not know whether that data comes from HTTP, a
database, or a test fake. The port captures the business dependency, and a
later adapter will supply the technical behavior.

## 16. Strong closing answer

Suggested answer:

I used Hexagonal Architecture to keep the business core independent from HTTP,
databases, and service-integration details. Ports let the use cases depend on
business abstractions, and adapters let infrastructure plug into those
abstractions without contaminating the core. That made the pricing and checkout
logic easier to test and easier to explain.

## 17. Are you using CQRS as well, or only Hexagonal Architecture?

Suggested answer:

Yes, but they solve different problems. Hexagonal Architecture is the main
structural pattern. It defines how the code is organized so the core stays
independent from infrastructure. CQRS is used in a lightweight way inside that
structure to separate write use cases from read use cases.

Project examples:

- command side: `CreateCartUseCase`, `AddCartItemUseCase`,
	`RemoveCartItemUseCase`, `CheckoutCartUseCase`
- query side: `GetCartUseCase`, `GetTopPurchasedProductsUseCase`

Important clarification:

- this project does not use heavy CQRS with separate databases, event sourcing,
	or brokers
- it uses CQRS mainly as a clean separation of intent between state-changing
	operations and read-only operations

## 18. What is the difference between Hexagonal Architecture and CQRS?

Suggested answer:

Hexagonal Architecture answers how the system is structured. CQRS answers how
operations are modeled. They are compatible and can be used together.

Simple explanation:

- Hexagonal Architecture: separate the core from infrastructure
- CQRS: separate writes from reads

How that looks in this project:

- `UserDirectoryPort` and `UserServiceHttpClientAdapter` show Hexagonal
	Architecture because the core depends on an abstraction and infrastructure
	implements it through HTTP
- `CreateCartUseCase` versus `GetCartUseCase` shows CQRS because write logic
	and read logic are modeled separately

## 19. Is `getUser` a command in this project?

Suggested answer:

No. `getUser` is a query or lookup, not a command. A command changes state,
while a query only returns data. In this case, `cart-service` asks for user
information so it can validate business rules such as VIP eligibility.

Short version:

- `getUser` is a read dependency
- `createCart` is a command
- `getCart` is a query

## 20. How would you explain ports, adapters, and CQRS together in one answer?

Suggested answer:

In this project, Hexagonal Architecture defines the boundaries and CQRS defines
the intent of use cases. The application layer exposes command use cases for
state changes and query use cases for reads. Those use cases depend on ports,
which are abstractions owned by the core. Infrastructure then provides
adapters, such as HTTP clients or controllers, to connect those use cases to
the outside world.