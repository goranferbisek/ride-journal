# Vehicle Maintenance & Driving Journal — Requirements

## 1. Overview

A personal web app for tracking vehicle maintenance and driving history. A user
maintains a "garage" of vehicles (cars, motorcycles, and other leisure vehicles)
and logs events against each vehicle (service, odometer readings, etc.). The app
visualizes odometer progression over time and yearly mileage per vehicle.

## 2. Actors

- **User** — the only actor. Each user's data (vehicles, events) is private to them.
  No admin role, no sharing between users in this version.

## 3. Domain Entities

### User
- `username` (unique)
- `passwordHash`
- No email required in this version (no email server).

### Vehicle
- `brand` (string)
- `model` (string)
- `type` — enum: `CAR`, `MOTORCYCLE`, `MOTORHOME`, `OTHER`
- `year` (optional, integer — year of manufacture)
- `licensePlate` (optional, string)
- `vin` (optional, string)
- belongs to one `User`

### VehicleEvent
- `date` (required)
- `eventType` — enum, predefined list (see below)
- `odometerKm` (optional, integer)
- `notes` (optional, free text)
- belongs to one `Vehicle`

### EventType (predefined enum)
- `OIL_CHANGE`
- `TIRE_CHANGE`
- `REPAIR`
- `REGISTRATION_RENEWAL`
- `INSPECTION`
- `INSURANCE`
- `ODOMETER_READING`
- `OTHER`

## 4. Functional Requirements

### 4.1 Authentication
- FR-1: A user can register with a username and password.
- FR-2: A user can log in with username/password.
- FR-3: A user can log out.
- FR-4: A logged-out user cannot access any vehicle/event data or screens.
- FR-5: Passwords are stored hashed (never in plaintext).
- FR-5a: Authentication uses JWTs issued on login and required on all
  protected API endpoints.

### 4.2 Vehicle Management
- FR-6: A user can view a list of their vehicles ("garage").
- FR-7: A user can add a new vehicle (brand, model, type).
- FR-8: A user can edit an existing vehicle's details.
- FR-9: A user can delete a vehicle. Deleting a vehicle **cascade-deletes**
  all of its events (hard delete, no recovery).
- FR-10: A user can only see and manage their own vehicles.

### 4.3 Event Management
- FR-11: A user can add an event to a vehicle with: date (required),
  event type (required, from predefined list), odometer reading (optional),
  notes (optional).
- FR-12: A user can view a single event's details.
- FR-13: A user can edit an existing event.
- FR-14: A user can delete an event.
- FR-15: A user can only see and manage events belonging to their own vehicles.

### 4.4 Dashboard
- FR-16: A user can select one vehicle to view on the dashboard.
- FR-17: The dashboard shows a line chart of odometer reading (km) over time
  (date on X axis, km on Y axis), using events that have an odometer value.
- FR-18: The dashboard shows total kilometers driven per calendar year for
  the selected vehicle, calculated as `max(odometer in year) − min(odometer
  in year)`. This considers **any** event in that year with an odometer
  value set, regardless of event type (e.g. a `REPAIR` or `INSPECTION`
  event with an odometer reading counts too — not just `ODOMETER_READING`
  events). A year with fewer than two events carrying an odometer value
  shows as "no data" rather than a calculated number.
- FR-19: The user can filter the chart and timeline by event type.
- FR-20: Below the chart, a timeline lists events for the selected vehicle
  (date, type, odometer, notes), respecting the same event-type filter as
  the chart. The user can narrow the timeline to a specific year-to-year
  period when it gets long (exact component — table vs. scrollable timeline
  — is a UI/LLD decision).
- FR-21: From the timeline, the user can open a single event to edit it
  (exact interaction — dialog vs. separate page — is a UI/LLD decision,
  not a hard requirement).

## 5. Non-Functional Requirements

- NFR-1: Backend is a Spring Boot monolith exposing a REST API.
- NFR-2: Frontend is a React + TypeScript single-page app, using MUI
  (Material UI) as the component library.
- NFR-3: Data is stored in a relational database.
- NFR-4: Odometer values are always in kilometers (no unit conversion needed).
- NFR-5: GitHub Actions CI runs automated tests and builds artifacts on
  push/PR.
- NFR-6: This is a demo/portfolio project — no email server, no password
  reset flow, no multi-tenant admin features in v1.

## 6. Screens (content only, not layout)

1. **Register** — username, password, submit.
2. **Login** — username, password, submit.
3. **Garage (vehicle list)** — list of user's vehicles, add-new action,
   edit/delete per vehicle.
4. **Add/Edit Vehicle** — form: brand, model, type, year (optional),
   license plate (optional), VIN (optional).
5. **Dashboard** — vehicle selector, event-type filter, odometer line chart,
   yearly km summary, timeline of events with year-to-year period filter,
   edit-event entry point.
6. **Add/Edit Event** — form: date, event type, odometer (optional), notes
   (optional).

## 7. Open Questions / Assumptions to revisit in HLD/LLD

None remaining — all resolved in the HLD/LLD.
