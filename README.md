# Cricket Academy Portal

A multi-role cricket academy management web application built using **Java, Spring Boot, and a browser-based UI**. The system enables administrators to manage batches and facilities, coaches to track player performance and manage fixtures, and players to view their statistics, analytics, and upcoming fixtures.

## Features

### Admin

* Create and manage player batches.
* Manage academy facilities.
* Schedule cricket fixtures.

### Coach

* Record and view player performance metrics.
* View billing summaries.
* Book net practice sessions.
* Manage fixture status: Scheduled, Live, Completed, or Cancelled.

### Player

* View personal performance statistics.
* Access 30-day performance analytics.
* View upcoming fixtures and metric history.

### Additional Features

* JWT-based authentication and role-based access control.
* Automatic demo data initialization on first startup.
* Fixture lifecycle management.
* Configurable asynchronous notifications.

## Tech Stack

| Technology            | Purpose                          |
| --------------------- | -------------------------------- |
| Java 17               | Backend programming              |
| Spring Boot 3.2.5     | Application framework            |
| Spring Web            | REST API development             |
| Spring Data JPA       | Database operations              |
| Spring Security       | Authentication and authorization |
| JWT (JJWT)            | Token-based authentication       |
| H2 Database           | In-memory data storage           |
| Lombok                | Boilerplate code reduction       |
| HTML, CSS, JavaScript | Frontend development             |
| Maven                 | Build and dependency management  |

## Prerequisites

Ensure the following are installed:

* [JDK 17 or newer](https://adoptium.net/)
* [Apache Maven 3.8 or newer](https://maven.apache.org/)

Verify the installations:

```bash
java -version
mvn -version
```

## Getting Started

### 1. Clone the repository

```bash
git clone <https://github.com/dheepthi-baskar/cricket-academy-portal>
cd cricket-academy-portal
```

### 2. Run the application

```bash
mvn spring-boot:run
```

Wait until the application starts successfully on port `8080`.

### 3. Open the application

Navigate to:

```text
http://localhost:8080
```

Keep the Spring Boot application running while using the web interface. Do not open the HTML files directly from the file system.

## Demo Accounts

The application automatically initializes demo data when the database is empty.

| Role   | Email                  | Password   |
| ------ | ---------------------- | ---------- |
| Admin  | `admin@academy.local`  | `password` |
| Coach  | `coach@academy.local`  | `password` |
| Player | `player@academy.local` | `password` |

Each role has access to its corresponding dashboard and authorized features.

**Note:** These credentials are for local demonstration only. Do not use demo credentials in a production environment.

## Application Pages

| Page             | URL            | Description                                     |
| ---------------- | -------------- | ----------------------------------------------- |
| Login            | `/`            | Authentication and role-based redirection       |
| Player Dashboard | `/player.html` | Performance statistics, analytics, and fixtures |
| Coach Dashboard  | `/coach.html`  | Player metrics, bookings, billing, and fixtures |
| Admin Dashboard  | `/admin.html`  | Batch, facility, and fixture management         |

## API Overview

**Base URL:** `http://localhost:8080`

### Authentication

| Method | Endpoint             | Description                          |
| ------ | -------------------- | ------------------------------------ |
| POST   | `/api/auth/login`    | Authenticate a user and return a JWT |
| POST   | `/api/auth/register` | Register a new user                  |

### Player APIs

| Method | Endpoint                   | Description                                     |
| ------ | -------------------------- | ----------------------------------------------- |
| GET    | `/api/player/me/metrics`   | Retrieve personal performance metrics           |
| GET    | `/api/player/me/analytics` | Retrieve performance analytics for a date range |
| GET    | `/api/player/me/fixtures`  | Retrieve personal fixtures                      |

### Coach APIs

| Method    | Endpoint                                | Description                   |
| --------- | --------------------------------------- | ----------------------------- |
| GET       | `/api/coach/players`                    | Retrieve players              |
| GET, POST | `/api/coach/players/{playerId}/metrics` | View or record player metrics |
| GET       | `/api/coach/facilities`                 | Retrieve facilities           |
| GET       | `/api/coach/bookings`                   | Retrieve bookings             |
| GET       | `/api/coach/billing/summaries`          | View billing summaries        |
| GET       | `/api/coach/fixtures`                   | Retrieve fixtures             |

### Admin APIs

| Method    | Endpoint                | Description               |
| --------- | ----------------------- | ------------------------- |
| GET, POST | `/api/admin/batches`    | View or create batches    |
| GET, POST | `/api/admin/facilities` | View or create facilities |
| POST      | `/api/admin/fixtures`   | Schedule a fixture        |

Additional coach endpoints support fixture lifecycle operations, including starting, completing, and cancelling fixtures.

### Authentication

Protected endpoints require a valid JWT in the request header:

```http
Authorization: Bearer <accessToken>
```

Example login request:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"player@academy.local\",\"password\":\"password\"}"
```

The login response includes the access token, user ID, and role.

## Project Structure

```text
src/
├── main/
│   ├── java/com/cricketacademy/
│   │   ├── bootstrap/    # Demo data initialization
│   │   ├── config/       # Application configuration
│   │   ├── domain/       # JPA entities
│   │   ├── repo/         # Spring Data repositories
│   │   ├── security/     # JWT and security configuration
│   │   ├── service/      # Business logic
│   │   └── web/          # REST controllers and DTOs
│   └── resources/
│       ├── application.yml
│       └── static/       # HTML, CSS, and JavaScript
└── test/                 # Automated tests, if present
```

## Build and Test

Run the automated tests:

```bash
mvn test
```

Compile the project:

```bash
mvn compile
```

Package the application into an executable JAR:

```bash
mvn package
```

Run the packaged application:

```bash
java -jar target/cricket-academy-portal-0.0.1-SNAPSHOT.jar
```

## Configuration

Application settings are maintained in `src/main/resources/application.yml`.

| Property                        | Default    | Description                    |
| ------------------------------- | ---------- | ------------------------------ |
| `server.port`                   | `8080`     | Application server port        |
| `jwt.expiration-ms`             | `86400000` | JWT validity period (24 hours) |
| `academy.notifications.enabled` | `true`     | Notification service toggle    |

The JWT signing secret must be configured securely for deployment. Never commit real secrets or production credentials to GitHub.

## Database

The application uses an **H2 in-memory database** for development and demonstration.

* Database: `cricket`
* JDBC URL: `jdbc:h2:mem:cricket`
* Username: `sa`
* Password: Empty by default

Data is stored in memory and resets when the application stops. Demo records are recreated on startup when the database is empty.

## Troubleshooting

| Problem                        | Solution                                                                        |
| ------------------------------ | ------------------------------------------------------------------------------- |
| Application does not start     | Verify JDK and Maven installations and check the application logs.              |
| Port 8080 is already in use    | Stop the conflicting process or change `server.port`.                           |
| API returns `401 Unauthorized` | Log in again and provide a valid JWT in the authorization header.               |
| Dashboard shows no data        | Sign in with a demo account and verify that demo data initialization completed. |


**Cricket Academy Portal** — Simplifying cricket academy administration and player performance tracking through a role-based web application.
