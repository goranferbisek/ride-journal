# Vehicle Maintenance & Driving Journal — Low-Level Design

## 1. Database Schema

### `users`
| Column | Type | Constraints |
|---|---|---|
| id | BIGSERIAL | PK |
| username | VARCHAR(50) | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| created_at | TIMESTAMP | NOT NULL, default now() |

### `vehicles`
| Column | Type | Constraints |
|---|---|---|
| id | BIGSERIAL | PK |
| user_id | BIGINT | FK → users.id, NOT NULL |
| brand | VARCHAR(100) | NOT NULL |
| model | VARCHAR(100) | NOT NULL |
| type | VARCHAR(20) | NOT NULL, enum: CAR / MOTORCYCLE / MOTORHOME / OTHER |
| year | INTEGER | nullable |
| license_plate | VARCHAR(20) | nullable |
| vin | VARCHAR(17) | nullable, CHECK (LENGTH(vin) = 17) when not null |
| created_at | TIMESTAMP | NOT NULL, default now() |
| updated_at | TIMESTAMP | NOT NULL, default now() |

Index: `(user_id)`

### `vehicle_events`
| Column | Type | Constraints |
|---|---|---|
| id | BIGSERIAL | PK |
| vehicle_id | BIGINT | FK → vehicles.id ON DELETE CASCADE, NOT NULL |
| event_date | DATE | NOT NULL |
| event_type | VARCHAR(30) | NOT NULL, enum: OIL_CHANGE / TIRE_CHANGE / REPAIR / REGISTRATION_RENEWAL / INSPECTION / INSURANCE / ODOMETER_READING / OTHER |
| odometer_km | INTEGER | nullable, CHECK (odometer_km >= 0) |
| notes | TEXT | nullable |
| created_at | TIMESTAMP | NOT NULL, default now() |
| updated_at | TIMESTAMP | NOT NULL, default now() |

Indexes: `(vehicle_id, event_date)`, `(vehicle_id, event_type)` — both support
the dashboard chart/timeline queries.

**Cascade note**: `ON DELETE CASCADE` on `vehicle_id` implements the
hard-delete requirement (FR-9) at the DB level, in addition to whatever the
service layer does.

## 2. REST API

All endpoints except `/api/auth/**` require `Authorization: Bearer <jwt>`.
All vehicle/event endpoints are scoped to the authenticated user — a request
for another user's vehicle/event returns 404 (not 403, to avoid leaking
existence).

### Auth
| Method | Path | Body | Response |
|---|---|---|---|
| POST | `/api/auth/register` | `{username, password}` | 201, `{id, username}` |
| POST | `/api/auth/login` | `{username, password}` | 200, `{token, expiresAt}` |

### Vehicles
| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/vehicles` | — | 200, `[VehicleDto]` |
| POST | `/api/vehicles` | `VehicleRequest` | 201, `VehicleDto` |
| GET | `/api/vehicles/{id}` | — | 200, `VehicleDto` |
| PUT | `/api/vehicles/{id}` | `VehicleRequest` | 200, `VehicleDto` |
| DELETE | `/api/vehicles/{id}` | — | 204 |

`VehicleRequest`: `{brand, model, type, year?, licensePlate?, vin?}`
`VehicleDto`: same fields + `id`, `createdAt`.

### Events
| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/vehicles/{vehicleId}/events?eventType=&fromYear=&toYear=&page=&size=` | — | 200, `Page<EventDto>` |
| POST | `/api/vehicles/{vehicleId}/events` | `EventRequest` | 201, `EventDto` |
| GET | `/api/vehicles/{vehicleId}/events/{eventId}` | — | 200, `EventDto` |
| PUT | `/api/vehicles/{vehicleId}/events/{eventId}` | `EventRequest` | 200, `EventDto` |
| DELETE | `/api/vehicles/{vehicleId}/events/{eventId}` | — | 204 |

`EventRequest`: `{date, eventType, odometerKm?, notes?}`
`EventDto`: same fields + `id`.
Query params `eventType`, `fromYear`/`toYear` are all optional filters;
omitting them returns everything (paged).

### Dashboard
| Method | Path | Response |
|---|---|---|
| GET | `/api/vehicles/{vehicleId}/odometer-history?eventType=` | 200, `[{date, odometerKm, eventType}]` — only events with `odometerKm` set, sorted by date ascending. Feeds the line chart. |
| GET | `/api/vehicles/{vehicleId}/yearly-km` | 200, `[{year, km}]` — `km` is `null` when fewer than 2 odometer-bearing events exist that year. |

### Error format (all endpoints)
```json
{
  "timestamp": "2026-08-10T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "odometerKm must be >= 0",
  "path": "/api/vehicles/3/events"
}
```
Implemented via a single `@RestControllerAdvice` global exception handler.

## 3. Validation Rules

| Field | Rule |
|---|---|
| username | required, 3–50 chars, unique |
| password | required, min 8 chars (enforce at registration) |
| vehicle.brand / model | required, non-blank |
| vehicle.type | required, must be a valid enum value |
| vehicle.year | optional, if present: 1900–current year+1 |
| vehicle.vin | optional, if present: exactly 17 chars |
| event.date | required, not in the future (reject future-dated events) |
| event.eventType | required, must be a valid enum value |
| event.odometerKm | optional, if present: >= 0. If lower than the vehicle's previous known odometer reading, the API still accepts it but returns a warning flag in the response (e.g. `{..., "warning": "odometer lower than previous reading"}`) so the frontend can surface a non-blocking notice to the user. |

## 4. Backend Structure (Gradle, Spring Boot)

```
src/main/java/com/goran/vehicletracker/
├── config/
│   ├── SecurityConfig.java        # HTTP security, JWT filter registration, CORS
│   │                               #   -> CorsConfigurationSource bean allowing
│   │                               #      the frontend's dev origin (e.g.
│   │                               #      http://localhost:5173), GET/POST/PUT/
│   │                               #      DELETE, and the Authorization header
│   └── OpenApiConfig.java         # (optional) Swagger/OpenAPI docs
├── security/
│   ├── JwtTokenProvider.java      # generate/validate/parse JWT
│   ├── JwtAuthFilter.java         # OncePerRequestFilter, reads Bearer token
│   └── UserDetailsServiceImpl.java
├── controller/
│   ├── AuthController.java
│   ├── VehicleController.java
│   └── VehicleEventController.java
├── service/
│   ├── AuthService.java
│   ├── VehicleService.java
│   ├── VehicleEventService.java
│   └── DashboardService.java      # odometer-history + yearly-km logic
├── repository/
│   ├── UserRepository.java
│   ├── VehicleRepository.java
│   └── VehicleEventRepository.java
├── entity/
│   ├── User.java
│   ├── Vehicle.java
│   ├── VehicleEvent.java
│   ├── VehicleType.java           # enum
│   └── EventType.java             # enum
├── dto/
│   ├── request/ (VehicleRequest, EventRequest, RegisterRequest, LoginRequest)
│   └── response/ (VehicleDto, EventDto, AuthResponse, ErrorResponse)
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    └── ValidationException.java
```

**Key implementation notes**:
- `JwtAuthFilter` runs once per request, extracts the user id from the
  token, and sets it in the `SecurityContext` so `@AuthenticationPrincipal`
  or `SecurityContextHolder` can be used in controllers/services to scope
  queries. `JwtTokenProvider` issues tokens with a 24-hour expiry
  (configurable via `application.yml`).
- Ownership checks live in the **service layer** (e.g.
  `VehicleService.getVehicleForUser(vehicleId, userId)` throws
  `ResourceNotFoundException` if the vehicle doesn't belong to the user) —
  never trust the controller alone.
- `DashboardService.getYearlyKm()`: group a vehicle's events (any type)
  that have `odometerKm != null` by `YEAR(event_date)`, then per year
  compute `max(odometerKm) - min(odometerKm)` if count >= 2, else `null`.
- Password hashing via Spring Security's `BCryptPasswordEncoder`.

## 5. Frontend Structure (React + TypeScript + MUI)

```
src/
├── api/
│   └── client.ts               # axios instance, request interceptor attaches JWT
├── types/
│   ├── vehicle.ts              # Vehicle, VehicleType, VehicleRequest
│   ├── event.ts                # VehicleEvent, EventType, EventRequest
│   └── auth.ts                 # AuthResponse, User
├── auth/
│   ├── AuthContext.tsx         # holds token/user, login()/logout()
│   └── ProtectedRoute.tsx      # redirects to /login if not authenticated
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── GaragePage.tsx          # vehicle list + add/delete
│   ├── VehicleFormPage.tsx     # add/edit vehicle (reused for both)
│   └── DashboardPage.tsx       # vehicle selector, chart, yearly km, timeline
├── components/
│   ├── VehicleCard.tsx
│   ├── OdometerChart.tsx       # MUI X Charts LineChart (odometer vs. date)
│   ├── YearlyKmSummary.tsx
│   ├── EventTimeline.tsx       # list/table + year-range filter
│   ├── EventTypeFilter.tsx
│   └── EventFormDialog.tsx     # MUI Dialog, add/edit event
├── routes/
│   └── AppRoutes.tsx           # React Router config
└── App.tsx
```

**Key implementation notes**:
- Routing: React Router; all routes except `/login` and `/register` wrapped
  in `ProtectedRoute`.
- Data fetching: React Query (`@tanstack/react-query`) recommended for
  caching + refetch-on-mutation (e.g. re-fetch chart data after adding an
  event) — simpler than hand-rolled `useEffect`/`useState` fetching.
- Charting library: MUI X Charts (`@mui/x-charts`) — stays within the MUI
  ecosystem, no extra charting dependency needed.
- Event editing: implement as a MUI `Dialog` opened from a row in
  `EventTimeline`, per your earlier note that this felt like the natural
  fit.
- JWT storage: in memory (React Context state), not localStorage, to
  reduce XSS exposure — this means a page refresh logs the user out until
  refresh tokens are added later, which is an acceptable v1 trade-off.

## 6. CI (GitHub Actions)

`.github/workflows/ci.yml`, two parallel jobs:

```yaml
name: CI
on:
  push:
    branches: [main]
  pull_request:

jobs:
  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
      - run: ./gradlew test build
      - uses: actions/upload-artifact@v4
        with:
          name: backend-jar
          path: build/libs/*.jar

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
      - run: npm ci
      - run: npx vitest run --passWithNoTests
      - run: npm run build
      - uses: actions/upload-artifact@v4
        with:
          name: frontend-dist
          path: dist/
```

**Note**: Vitest is wired into CI now so the pipeline is ready, but no
frontend tests are planned for the initial implementation — `--passWithNoTests`
keeps the job green until tests are added later.

## 7. Local Development

- Postgres via `docker-compose.yml` (single service, exposed on 5432).
- Backend: run from IntelliJ against local Postgres; `application-dev.yml`
  for local DB credentials. Runs on port 8080.
- Frontend: `vite` dev server on its own port (e.g. 5173). No proxy —
  the backend exposes explicit CORS instead (see §4), so frontend and
  backend can run on different ports independently.

## 8. Implementation Notes Resolved

- Odometer regression: allowed, but the API returns a non-blocking warning
  flag on the response (see §3) for the frontend to display.
- JWT expiry: 24 hours.
