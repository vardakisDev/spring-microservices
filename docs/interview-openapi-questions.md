# OpenAPI Interview Questions

This file collects likely interview questions about why OpenAPI was used in
this project and how it helped the implementation.

## 1. Why did you use OpenAPI in this project?

Suggested answer:

I used OpenAPI because the assessment explicitly requires an API-first
approach, where the contract is the source of truth. Instead of starting from
controllers and letting the API emerge from the code, I defined the endpoints,
request and response schemas, validation rules, and security requirements up
front. That made the service boundary clear before implementation started.

Short version:

- the brief required API-first design
- it made the service contract explicit early
- it reduced drift between API documentation and code

## 2. What did OpenAPI actually generate for you?

Suggested answer:

OpenAPI generated the transport-layer scaffolding, not the business logic. In
this project it generated API interfaces, request and response models, Bean
Validation annotations, and OpenAPI annotations for the HTTP layer. I still had
to implement the real application behavior myself.

Important distinction:

- generated: endpoint interfaces and DTOs
- handwritten: domain logic, use cases, repositories, persistence, security
  enforcement, and service-to-service integrations

## 3. Why is that useful if it does not generate the full application?

Suggested answer:

It removes repetitive boundary code and keeps the contract authoritative. The
main value is not code volume, it is consistency. The generated API layer stays
aligned with the contract, and the implementation is forced to match the agreed
endpoint signatures, schemas, and status codes.

## 4. How did OpenAPI help the design of the system?

Suggested answer:

It forced me to think about the API before implementation details. That helped
me define clear service ownership, error handling, validation constraints, and
security rules. In a split microservice setup, that matters because each
service should expose a clear contract that other services or clients can rely
on.

## 5. How does OpenAPI fit with Hexagonal Architecture?

Suggested answer:

OpenAPI belongs at the edge of the system. It defines the HTTP contract for the
inbound adapter. The generated interfaces and models sit in the transport
boundary, while the application and domain layers remain handwritten and free
from transport concerns. That separation is exactly what Hexagonal Architecture
tries to achieve.

Simple explanation:

- OpenAPI defines the outer API boundary
- generated code supports the inbound adapter
- domain and use cases stay independent from HTTP details

## 6. Why not just write the controllers and DTOs manually?

Suggested answer:

That would work technically, but it would weaken the API-first discipline. If I
write controllers first, the code can become the source of truth and the spec
can drift. By generating the interface from OpenAPI, I make the contract the
thing that drives implementation rather than the other way around.

## 7. Did you write the generated interfaces yourself?

Suggested answer:

No. The generated HTTP interfaces should not be handwritten or manually edited.
They come from the OpenAPI contract. What I do write myself are the classes
that implement those interfaces, plus the application ports and domain
abstractions required by the hexagonal design.

Important distinction:

- generated API interfaces: do not handwrite them
- handwritten architecture interfaces: repository ports, client ports, use case
  boundaries, and similar abstractions

## 8. What is the purpose of security definitions in OpenAPI?

Suggested answer:

The security definition makes authentication and authorization part of the API
contract, not just an internal implementation detail. In this project the
contract declares a Bearer JWT scheme and protected operations, plus explicit
401 and 403 responses. That improves clarity for consumers and helps produce a
more robust, audit-friendly API specification.

## 9. How did OpenAPI help with security in this project?

Suggested answer:

It made security requirements visible early. Instead of adding protection late,
I declared the BearerAuth scheme, protected endpoints, and explicit error
responses directly in the contract. That creates a clear link between the API
design and the eventual Spring Security implementation.

## 10. Why is OpenAPI especially useful in microservices?

Suggested answer:

Because each service should own a clear and versioned API contract. In a
microservice system, services interact through boundaries, so the contract is a
first-class artifact. OpenAPI helps keep those boundaries explicit, testable,
and understandable across teams or services.

## 11. Is OpenAPI only useful for external clients?

Suggested answer:

No. It is useful for both external and internal consumers. In a microservice
system, other services are also consumers. Even if an API is internal, having a
strong contract improves consistency, integration safety, and onboarding.

## 12. What is the limitation of relying on OpenAPI generation?

Suggested answer:

It does not replace software design. OpenAPI can define the contract and
generate the transport scaffolding, but it cannot design the domain model,
choose the right consistency model, implement business rules, or structure the
hexagonal architecture correctly. Those are still engineering decisions.

## 13. How did OpenAPI help specifically in this project?

Suggested answer:

In this project, it helped in four concrete ways:

1. It satisfied the assessment requirement for API-first design.
2. It generated the HTTP interface and models for the cart-service contract.
3. It made security and validation explicit in the contract.
4. It made the microservice split clearer by giving each service its own API
   boundary.

## 14. What would you say if the interviewer asks whether OpenAPI was worth it?

Suggested answer:

Yes, because the project benefits from a strong and explicit service contract.
The amount of generated code is not the main advantage. The real value is that
the API is designed intentionally, shared clearly, validated early, and kept in
sync with the implementation.

## 15. Strong closing answer

Suggested answer:

I used OpenAPI to make the contract explicit before implementation, generate the
boundary-layer scaffolding, keep documentation and code aligned, and surface
security and validation requirements early. It fits both the assessment's
API-first requirement and the architectural goal of keeping transport concerns
at the edge of the system.