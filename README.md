# Golden Ticket Drop

A full-stack case study simulating a high-concurrency limited inventory drop
(e.g. exclusive tickets or limited-edition products).

The system guarantees **no overselling** under concurrent load and provides
clear user feedback during purchase attempts.


## Tech Stack

**Frontend**
- React + TypeScript
- Vite

**Backend**
- Java 17
- Spring Boot
- In-memory data store (ConcurrentHashMap + AtomicInteger)

---


## Part A – Architecture & Design

### 1) Architecture Diagram

![img.png](img.png)

**Flow**
1. The frontend loads product and inventory
3. The frontend polls inventory periodically to keep the UI updated
4. When the user clicks “Buy now”, a purchase request is sent to the backend
5. The backend attempts to decrement inventory atomically and returns the result


### 2) Concurrency Strategy (No Overselling)

The backend guarantees that inventory never drops below zero, even under
high concurrency.

#### Current MVP (In-Memory)

Inventory is stored using:
- `ConcurrentHashMap<Integer, AtomicInteger>` for per-product stock
- Atomic decrement via `AtomicInteger#getAndUpdate`

Why this works

- getAndUpdate is a single atomic operation.
- Multiple concurrent requests may attempt to buy at the same time. Only one request can observe previous = 1 when stock is 1. 
  All others observe previous = 0 and fail gracefully.
- Inventory can never become negative.

This approach avoids:
- synchronized blocks
- explicit locking
- race conditions caused by “check-then-act” logic


3) Scalability (1 Million Concurrent Users)

At extreme contention (millions of concurrent buyers), the bottleneck is a single inventory row update. Moving the hot-path decrement to Redis (atomic counter) 
reduces database lock contention and latency, allowing horizontal scaling of stateless API instances, while the database remains the durable source of truth for purchase records.
Application can be deployed as stateless containers behind a load balancer with autoscaling.
Cloud technology can be used to efficiently utilize resources and scale in high-traffic situations.

- Use Redis atomic operations to reserve inventory.
- Persist results asynchronously to database.


## How to Run Locally

### Backend

Requirements:
- Java 17
- Maven

Run with IntelliJ **or** from terminal:

go to project location
- ./mvnw clean test
- ./mvnw spring-boot:run
- runs on: http://localhost:8080



### Frontend
Requirements:
- Node

go to project location
- npm install
- npm run dev
- Frontend runs on: http://localhost:5173

NOTES: The backend CORS config allows http://localhost:5173, http://localhost:5174.


## Architectural Decisions

- In-memory storage was used for the MVP to focus on concurrency correctness.
- AtomicInteger provides atomic read-modify-write operations that are safe under concurrent access. 
  it is used specifically to avoid “check-then-act” bugs.Atomic counters were chosen over synchronized blocks to avoid coarse-grained locking.
-  The system supports multiple product types.So inventory stored per product in a ConcurrentHashMap so that reads are thread-safe,
   updates to different products don’t block each other and the structure remains safe as traffic increases
- Inventory is periodically refreshed via polling.
- User-triggered messages (success, sold out, error, not found).
- Unit test added to test over selling.

Example of REST requests:
- GET products : http://localhost:8080/products
- GET golden ticket inventory http://localhost:8080/products/1/inventory
- POST /buy 
Request Body
{
"productId": 1,
"quantity": 1
}


## Known Limitations / If I Had More Time

Inventory is stored in memory and resets on application restart. This is an intentional trade-off for the MVP, 
allowing the focus to remain on concurrency correctness rather than persistence.
A production-ready system would persist inventory state in an external store.

NOTE: Golden ticket product id set as 1.

The user interface prioritizes clarity over visual polish and can be further improved.
Additional work could enhance layout, styling, and accessibility for a production environment.

If this application were a production, these are can be implemented:
•	Replace polling with server-sent events or WebSockets.
•	Redis for inventory reservation.
•	Idempotency keys for purchase requests.
•	Authentication
•   Persist inventory state in an external store.
•	Atomic SQL updates in database
