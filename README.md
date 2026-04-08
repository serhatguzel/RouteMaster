# ✈️ RouteMaster — Aviation Route Management System

A web application for managing locations, transportation options, and calculating valid aviation routes.

## 📌 Project Goals

RouteMaster is a system that manages the transportation network between airports and city points and calculates valid route combinations based on the **Transfer → Flight → Transfer** logic.

### Key Features

- **Location Management** — Add, update, and delete airports and other transfer points.
- **Transportation Management** — Manage flight/bus/subway/uber connections between two locations.
- **Smart Route Search** — Discover valid routes based on selected dates and locations using a DFS algorithm.
- **Role-Based Access Control** — ADMIN (full access) and AGENCY (search only) roles.
- **Redis Caching** — Frequently used data is stored in memory for high performance.

## 🏗️ System Architecture

```
┌──────────────────────────────────────────────────────┐
│                   FRONTEND (React)                    │
│   Login  │  Locations  │  Transportations  │  Routes  │
└─────────────────────────┬────────────────────────────┘
                          │ HTTP / REST API
          ┌───────────────▼────────────────┐
          │   Spring Security (JWT Filter)  │  ← Token validated
          │   Return 401  or  Authorize ──► │    on every request
          └───────────────┬────────────────┘
                          │ Identity Authenticated
┌─────────────────────────▼─────────────────────────────┐
│                 BACKEND (Spring Boot)                 │
│                                                       │
│        ┌──────────────────────────────────┐           │
│        │       Controller  (Entry Point)  │           │
│        │                                  │           │
│        └─────────────────┬────────────────┘           │
│                          │                            │
│        ┌─────────────────▼────────────────┐           │
│        │         Service (Business Logic) │           │
│        └──────┬──────────────────┬─────────┘          │
│               │                  │                    │
│           (MISS)    ┌────────────▼──────────┐         │
│               │     │          Redis         │        │
│               │     │          Cache         │        │
│               │     └────────────┬──────────┘         │
│    ┌──────────▼──┐          (HIT)│ returns directly   │
│    │  Repository │               │                    │
│    │ (DB Access) │               │                    │
│    └──────┬──────┘               │                    │
└───────────┼───────────────────────────────────────────┘
             │
    ┌────────▼───────────────────────┐
    │     H2 In-Memory Database      │
    │  (Liquibase schema management) │
    └────────────────────────────────┘
```

## Routing Algorithm

The system finds valid route combinations with a maximum of **3 segments**:

Valid Formats:
─────────────────────────────────────────────

  [F]                  Flight Only

  [T] ──► [F]          Transfer + Flight

  [F] ──► [T]          Flight + Transfer

  [T] ──► [F] ──► [T]  Transfer + Flight + Transfer

─────────────────────────────────────────────
  F = FLIGHT  |  T = Transfer (BUS / SUBWAY / UBER)

**Rules:**
- A route can contain **exactly 1 flight**.
- There can be **at most 1 ground transfer** before or after the flight.
- Only operational transports that match the day of the week of the selected date are included.

## Technologies Used

### Backend
| Technology | Version | Purpose |
|-----------|-------|-------|
| Java | 21 | Programming Language |
| Spring Boot | 3.5.x | Application Framework |
| Spring Security | — | JWT Authentication & Authorization |
| Spring Data JPA | — | ORM and Database operations |
| Spring Data Redis | — | Caching and Refresh Token storage |
| H2 Database | — | In-memory Database |
| Liquibase | — | Database Schema Versioning |
| JJWT | 0.12.6 | JSON Web Token generation & validation |
| MapStruct | 1.5.5 | Entity ↔ DTO conversion automation |
| Lombok | — | Boilerplate code reduction |
| SpringDoc OpenAPI | 2.8.x | Swagger UI |

### Frontend
| Technology | Purpose |
|-----------|-------|
| React 18 | UI Library |
| React Router | Page Routing |
| Axios | HTTP Requests and Interceptors |
| MUI (Material UI) | DatePicker Component |
| Lucide React | Icon set |
| Tailwind CSS | Styling |

### Infrastructure
| Technology | Purpose |
|-----------|-------|
| Docker | Containerization |
| Docker Compose | Multi-service orchestration |
| Redis 7.2 | Cache and Token store |

## User Roles and Permissions

| Role | Location Management | Transportation Management | Route Search |
|-----|:--------------:|:---------------:|:----------:|
| **ADMIN** | ✅ | ✅ | ✅ |
| **AGENCY** | ❌ | ❌ | ✅ |

### Default Credentials (Development)
| Username | Password | Role |
|---------------|-------|-----|
| `admin` | `admin123` | ADMIN |
| `agency` | `agency123` | AGENCY |

---

## 🚀 Getting Started

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- Node.js

### 1️⃣ Backend + Redis — Start with Docker

Open a terminal in the project root directory:

```bash
# Build and start for the first time
docker-compose up --build -d

# Subsequent starts (no rebuild needed)
docker-compose up -d

# To stop the services
docker-compose down
```

> The Backend will start at `http://localhost:8080`.
> Redis will run on port `6379`.

---

### 2️⃣ Frontend — Development Server

```bash
# Enter the frontend folder
cd frontend

# Install dependencies (first time)
npm install

# Start the dev server
npm run dev
```

> The Frontend will start at `http://localhost:5173`.

---

### 3️⃣ Accessing the Application

Open `http://localhost:5173` in your browser and log in with the default credentials.

---

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|----------|
| POST | `/api/auth/login` | Login and receive tokens |
| POST | `/api/auth/refresh` | Refresh Access Token |

### Location Management (ADMIN)
| Method | Endpoint | Description |
|--------|----------|----------|
| GET | `/api/v1/locations` | List all locations |
| GET | `/api/v1/locations/{id}` | Get location by ID |
| POST | `/api/v1/locations` | Add new location |
| PUT | `/api/v1/locations/{id}` | Update location |
| DELETE | `/api/v1/locations/{id}` | Delete location |

### Transportation Management (ADMIN)
| Method | Endpoint | Description |
|--------|----------|----------|
| GET | `/api/v1/transportations` | List all transportations |
| GET | `/api/v1/transportations/{id}` | Get single transportation |
| POST | `/api/v1/transportations` | Add new transportation |
| PUT | `/api/v1/transportations/{id}` | Update transportation |
| DELETE | `/api/v1/transportations/{id}` | Delete transportation |

### Route Search (ADMIN + AGENCY)
| Method | Endpoint | Description |
|--------|----------|----------|
| GET | `/api/v1/routes/search?originId=&destinationId=&date=` | Search for routes |

## Developer Tools

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```
Can be used to test all API endpoints.

### H2 Database Console
```
http://localhost:8080/h2-console

JDBC URL : jdbc:h2:mem:routemasterdb
User     : root
Password : root
```

### Flush Redis (Reset Cache)
```bash
docker exec routemaster_redis redis-cli FLUSHALL
```

---

## 🐳 Docker Environment

```yaml
# docker-compose.yml summary
services:
  routemaster-api:   # Spring Boot — port 8080
  redis:             # Redis 7.2  — port 6379
```

The Dockerfile uses a **multi-stage** build:
- **Stage 1 (builder):** Compiles the project using Maven.
- **Stage 2 (runner):** Runs the application using a minimal Alpine JRE image.

---

## 📝 Notes

- Since the database is **in-memory (H2)**, all data is reset whenever the application restarts. the `DataInitializer` automatically loads seed data.
- The JWT **Access Token** is valid for 15 minutes. When it expires, the Axios interceptor automatically renews it using the **Refresh Token**.
- Refresh Tokens are stored in **Redis** and are valid for 7 days.
