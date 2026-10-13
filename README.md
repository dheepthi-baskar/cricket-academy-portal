# Cricket Academy Portal

A multi-role cricket academy management application built with **Spring Boot 3** and a browser-based UI. Coaches track player performance, admins manage batches and facilities, and players view their stats, fixtures, and analytics.

## Features

| Role | Capabilities |
|------|----------------|
| **Admin** | Create batches, facilities, and scheduled fixtures |
| **Coach** | Record performance metrics, view billing summaries, book net sessions, manage fixture lifecycle (scheduled → live → completed/cancelled) |
| **Player** | View personal metrics, 30-day analytics, and upcoming fixtures |

Additional capabilities:

- JWT-based authentication (stateless API)
- In-memory H2 database with demo seed data on first startup
- Match fixture state machine (`SCHEDULED`, `LIVE`, `COMPLETED`, `CANCELLED`)
- Optional async notifications (configurable)

## Tech stack

- **Java 17**
- **Spring Boot 3.2.5** — Web, Data JPA, Security, Validation
- **H2** — in-memory database (development)
- **JJWT** — JSON Web Tokens
- **Lombok**
- **Static UI** — HTML, CSS, JavaScript (served from `src/main/resources/static`)

## Prerequisites

- [JDK 17](https://adoptium.net/) or newer
- [Apache Maven 3.8+](https://maven.apache.org/)

## Quick start

### 1. Run the application

```bash
mvn spring-boot:run
```

Wait until the log shows:

```text
Tomcat started on port 8080 (http)
```

### 2. Open in a browser

Use **Chrome**, **Edge**, **Firefox**, or any modern browser:

```text
http://localhost:8080
```

> **Important:** Open the URL above while the server is running. Do not open HTML files directly from disk (`file://`); the UI calls the API on `localhost:8080`.

### 3. Sign in with demo accounts

All demo users share the password: **`password`**

| Role   | Email                 |
|--------|------------------------|
| Admin  | `admin@academy.local`  |
| Coach  | `coach@academy.local`  |
| Player | `player@academy.local` |

The login page includes quick-fill buttons for these accounts.

## Web UI

| Page | URL | Description |
|------|-----|-------------|
| Login | `http://localhost:8080/` | Sign in; redirects by role |
| Player dashboard | `/player.html` | Stats, fixtures, metric history |
| Coach dashboard | `/coach.html` | Metrics, fixtures, billing, net bookings |
| Admin dashboard | `/admin.html` | Batches, facilities, fixtures |

Static assets live under `src/main/resources/static/`.

## API overview

Base URL: `http://localhost:8080`

### Authentication (public)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/login` | Returns JWT (`accessToken`, `userId`, `role`) |
| `POST` | `/api/auth/register` | Register a new user |

**Example login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"player@academy.local\",\"password\":\"password\"}"
```

Use the token on protected routes:

```http
Authorization: Bearer <accessToken>
```

### Player (`ROLE_PLAYER`)

| Method | Path |
|--------|------|
| `GET` | `/api/player/me/metrics` |
| `GET` | `/api/player/me/analytics?from=YYYY-MM-DD&to=YYYY-MM-DD` |
| `GET` | `/api/player/me/fixtures` |

### Coach (`ROLE_COACH` or `ROLE_ADMIN`)

| Method | Path |
|--------|------|
| `GET` | `/api/coach/players` |
| `GET` | `/api/coach/facilities` |
| `GET` / `POST` | `/api/coach/players/{playerId}/metrics` |
| `GET` | `/api/coach/billing/summaries` |
| `GET` / `POST` | `/api/coach/bookings`, `/api/coach/bookings/mine` |
| `GET` | `/api/coach/fixtures` |
| `POST` | `/api/coach/fixtures/{id}/go-live` |
| `POST` | `/api/coach/fixtures/{id}/complete` |
| `POST` | `/api/coach/fixtures/{id}/cancel` |

### Admin (`ROLE_ADMIN`)

| Method | Path |
|--------|------|
| `GET` / `POST` | `/api/admin/batches` |
| `GET` / `POST` | `/api/admin/facilities` |
| `POST` | `/api/admin/fixtures` |

## H2 database console

When the app is running:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:cricket`
- Username: `sa`
- Password: *(empty)*

Data is in-memory and resets when the application stops.

## Configuration

Key settings in `src/main/resources/application.yml`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | HTTP port |
| `jwt.secret` | *(see file)* | Signing key — change in production |
| `jwt.expiration-ms` | `86400000` | Token lifetime (24 hours) |
| `academy.notifications.enabled` | `true` | Toggle notification service |

## Project structure

```text
src/main/java/com/cricketacademy/
├── bootstrap/          # Demo data seeder
├── config/             # Application & async configuration
├── domain/             # JPA entities (users, players, batches, fixtures, …)
├── repo/               # Spring Data repositories
├── security/           # JWT filter, Spring Security config
├── service/            # Business logic
└── web/                # REST controllers & DTOs

src/main/resources/
├── application.yml
└── static/             # Web UI (HTML, CSS, JS)
```

## Build & test

```bash
# Compile
mvn compile

# Run tests
mvn test

# Package JAR
mvn package
java -jar target/cricket-academy-portal-0.0.1-SNAPSHOT.jar
```

## Demo seed data

On first startup, `AcademyDataSeeder` creates:

- Users: admin, coach, player (password `password`)
- Batch: **Under-19 Elite** (coach-assigned)
- Player profile linked to the player user
- Facility: **Centre Indoor Nets**
- Sample performance metrics (5 sessions)
- A billing statement and a scheduled practice fixture

Seeding runs only when the database is empty.

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Browser shows “can’t reach this page” | Start the app with `mvn spring-boot:run` and use `http://localhost:8080` |
| Login works in UI but no data | Ensure you use a demo account; reseed by restarting with an empty DB |
| `401 Unauthorized` on API calls | Send `Authorization: Bearer <token>` from `/api/auth/login` |
| Port 8080 in use | Stop the other process or set `server.port` in `application.yml` |


