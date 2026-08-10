# Ride Journal

A personal web app for tracking vehicle maintenance and driving history.

## Local Development

Start a local PostgreSQL 18 database via Docker Compose:

```bash
docker compose up -d
```

This starts a `postgres` container reachable at `localhost:5432` with:

- **Database**: `rideJournal`
- **User**: `rideJournal`
- **Password**: `rideJournal`

Stop it with:

```bash
docker compose down
```
