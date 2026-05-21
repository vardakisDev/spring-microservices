**Shopping Cart Service (API‑First, Hexagonal, PostgreSQL)**

**Goal**

You are required to build a **Spring Boot microservice** that implements a basic shopping cart system, following an API-first approach and clean architectural principles.

**Technical Requirements**

The solution must comply with the following:

- Use **Spring Boot**
- Follow a **Hexagonal Architecture** structure (domain, application, infrastructure)
- Use **PostgreSQL** for persistence (in-memory databases such as H2 are not allowed)
- Follow an **API-first approach**, where the API contract is the source of truth
- Define the full API contract in an openapi/openapi.yaml file
- Generate the Java code from the OpenAPI definition using the project's generator setup
- Include proper **security definitions** in the OpenAPI contract
- Ensure the OpenAPI specification achieves a **42Crunch Security Audit score of 100**
- Use Sonar

**Approach**

You should first design the API contract and only then proceed with the implementation. The expected flow is:

- Fully define the API in openapi.yaml
- Ensure it passes the 42Crunch audit with a score of 100
- Generate the code from the OpenAPI definition
- Implement the business logic on top of the generated structure

**Functional Requirements**

The service should support the following capabilities:

You need to implement CRUD operations for both **users** and **products**.

The system must allow creating, retrieving, and deleting shopping carts. Additionally, users must be able to add and remove products from a cart.

The cart should expose its current state, including all items, their quantities, and the total amount to be paid after applying any relevant pricing rules.

A checkout operation must be implemented to finalize a cart. This step does not involve any payment processing; it simply persists the contents of the cart as part of the user's purchase history.

Finally, the service must provide a way to retrieve the **four most expensive products purchased by a given user**, based on their historical purchases.

**Business Rules**

Each cart must be created as either **NORMAL** or **VIP**. The rule determining the cart type is up to you (for example, a flag on the user), but it must be clearly documented. Once a cart is created, its type cannot change.

When calculating the total price of a cart, the following promotion rules must be applied:

- If the cart contains exactly 5 items (sum of quantities equals 5), a 20% discount must be applied to the subtotal.
- If the cart contains more than 10 items:
  - For a NORMAL cart, apply a €20 discount to the subtotal.
  - For a VIP cart, the cheapest item becomes free (subtract its unit price once), and an additional €70 discount must be applied.

**Database**

All data must be persisted using **PostgreSQL**. The use of in-memory databases is not allowed.

**Security**

Security must be defined at the API level and reflected in the OpenAPI contract (for example, using a Bearer token scheme or equivalent). The API should include proper input validation and be designed to comply with **42Crunch requirements**, achieving a full score of 100.

**Testing**

You are expected to include unit tests covering at least:

- The pricing rules
- Core domain logic and main use cases

**Delivery**

Your submission should include:

- The complete working project
- The OpenAPI YAML file
- The generated code along with your implementation
- A short README explaining how to run the service and any relevant design decisions or assumptions