# HomeService Pro Booking - Presentation Slides

## Slide 1: Application Overview
- **Application Name**: HomeService Pro Booking
- **Problem Statement**: 
  - Fragmentation in the home service market makes it difficult for customers to find reliable professionals (plumbers, cleaners) and for providers to manage their schedules efficiently.
  - "HomeService Pro" bridges this gap with a centralized platform for booking, tracking, and managing home services.
- **Why Microservices?**:
  - **Scalability**: The "Booking" and "Provider" domains have different load patterns. Microservices allow independent scaling (e.g., scaling `customer-service` during high traffic without touching `job-service`).
  - **Independent Deployment**: Features like a new "Provider UI" can be deployed without redeploying the entire customer-facing application.
  - **Resilience**: A failure in the "Notification" system (future scope) wouldn't take down the core "Booking" flow.

## Slide 2: Microservices Architecture
- **Core Services**:
  1.  **customer-service**: Manages customer profiles, authentication, and the browsing/booking UI.
  2.  **serviceprovider-service**: Manages provider profiles, availability, and the job acceptance UI.
  3.  **job-service**: The central engine for job lifecycle (Booking -> Pending -> Accepted -> Completed).
- **Supporting Services**:
  1.  **eureka-server**: Service Registry for dynamic discovery.
  2.  **config-server**: Centralized configuration management.
  3.  **api-gateway**: Single entry point for routing and filtering.
- **Communication**:
  - **Synchronous**: REST API calls using **Spring Cloud OpenFeign**.
  - **Database per Service**: Strict isolation; each service owns its data (H2/SQL).

## Slide 3: System Architecture Overview
*(See Architecture Diagram in next slide)*
- **Flow**:
  1.  **Client (Browser)** sends a request (e.g., `/book`) to the **API Gateway**.
  2.  **Gateway** routes it to `customer-service`.
  3.  **Customer Service** validates the user and sends a booking request via **Feign Client** to `job-service`.
  4.  `job-service` stores the job and marks it as "PENDING".
  5.  **Provider** logs in via `serviceprovider-service` (routed by Gateway).
  6.  `serviceprovider-service` fetches pending jobs from `job-service` and displays them.
- **Infrastructure**:
  - All services register with **Eureka** on startup.
  - All services pull sensitive/environment configs from **Config Server**.

## Slide 4: Service Discovery & Configuration
- **Service Discovery (Eureka)**:
  - **Role**: Eliminated hardcoded URLs. Services register by name (`JOB-SERVICE`).
  - **Mechanism**: Clients (Gateway, Customer Service) query Eureka to find the IP/Port of target services.
  - **Benefit**: Dynamic scaling (new instances are automatically discovered).
- **Centralized Configuration (Config Server)**:
  - **Role**: Manages `application.yml` for all services in one place (`config-repo`).
  - **Mechanism**: Services contact Config Server at startup to load DB URLs, toggles, and secrets.
  - **Benefit**: "Configuration as Code" and consistency across environments (Dev/Prod).

## Slide 5: API Gateway
- **Role**: The "Front Door" of the system.
- **Key Functions**:
  - **Routing**: Map `/customer/**` -> `customer-service`, `/provider/**` -> `serviceprovider-service`.
  - **Load Balancing**: Distributes traffic among multiple instances of a service.
  - **Security**: Can implement global rate limiting, CORS, and authentication filters.
- **Implementation**: Built with **Spring Cloud Gateway**.
