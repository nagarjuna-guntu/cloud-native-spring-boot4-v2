# Book Order System Architecture

The **Book Order System** is a containerized, cloud-native application built on a distributed microservices model. 
It relies on a decoupled, reactive architecture driven by asynchronous events and secured through unified OAuth2/OIDC identity management.

---

## 🏗️ System Components

### 🔀 1. Stateless Microservices
Each component is independently built, containerized using **Docker**, and can be scaled horizontally.

* **`book-edge-service`**
  * **Technology:** Spring Cloud WebFlux (Reactive)
  * **Role:** API Gateway acting as the single entry point for all traffic. It acts as the **OAuth2 Client**,
  * managing user sessions and cookie-to-token translations. Policy enforcements apart from routing rules added security
  * policies and fault-tolarant capabilites using resillience-4j
* **`book-catalog-service`**
  * **Technology:** Spring Boot
  * **Role:** Manages the book inventory, catalog data, and book details. Acts as an **OAuth2 Resource Server**.
* **`book-order-service`**
  * **Technology:** Spring Boot
  * **Role:** Handles purchasing, order placement, and tracking lifecycle status. Acts as an **OAuth2 Resource Server**.
* **`book-dispatch-service`**
  * **Technology:** Spring Boot
  * **Role:** Executes post-order logistics, handles shipping actions, and updates fulfillment details.

### 🗄️ 2. Stateful Infrastructure Services
The backing data layer implements strict isolation using the **Database-per-Service** pattern alongside caching and event messaging frameworks.

* **PostgreSQL**
  * Dedicated database instances isolated per service. The `book-catalog-service` and `book-order-service` use completely separate databases to enforce data boundaries.
* **Redis**
  * Serves as a distributed **Cache-Aside** mechanism for low-latency lookups.
  * Handles backend distributed session storage via **Spring Session** to manage authorization token mapping with cookie sessions.
* **RabbitMQ**
  * The central **Event Broker** integrated via **Spring Cloud Stream** to enable loose coupling, resilient retries, and eventual consistency across domains.

---

## 🔐 Security Framework (OAuth2 / OIDC)

The entire system operates on a zero-trust model utilizing **Keycloak** as the central Identity Provider (IdP).

* **Authentication Flow:** The user's browser interacts with the system through the **Authorization Code Grant Type** over OpenID Connect (OIDC).
* **Identity Context:** The `book-edge-service` terminates the public session cookie and safely propagates user contexts downstream to the Resource Servers
* (`book-catalog-service` and `book-order-service`) via securely signed **JWT Bearer Tokens**.

---

## 🔄 Asynchronous Event-Driven Flows

Interaction across services is largely non-blocking and driven by messages flowing through **RabbitMQ**.

### Order Fulfillment Lifecycle
1. **`book-order-service`** commits an order to its database → emits an `Order Accepted` event.
2. **`book-dispatch-service`** consumes the `Order Accepted` event → processes shipping → emits an `Order Dispatched` event.
3. **`book-order-service`** consumes the dispatch event → updates the order record as "dispatched" in its PostgreSQL database.

### Cache Synchronization Lifecycle
1. **`book-catalog-service`** commits a book creation or update to its PostgreSQL database.
2. Broadcasts a `Book Created` or `Book Updated` event out-of-band to immediately update or invalidate data stored inside the **Redis** cluster.

---

## 📊 Enterprise Observability Suite

The entire infrastructure uses automated telemetry collection to track health and performance issues.

* **Telemetry Pipeline:** Every stateless microservice is bundled with the **OpenTelemetry (OTel) Starter**
*  to continuously emit structured logs, metrics, and distributed traces.
* **Monitoring Cluster:** Uses the **Grafana LGTM stack** (Loki, Grafana, Tempo, Mimir) as a centralized backend to store,
* search, and visually graph system diagnostics and traces.
