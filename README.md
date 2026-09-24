# Personal Finance Dashboard

An Open Banking personal finance dashboard built with **Spring Boot + React**, using **TrueLayer's UK Open Banking API** to connect to real (sandbox) bank accounts, sync transactions, automatically categorise spending, detect recurring payments, and track budgets.

![CI](https://github.com/Ali627miya/Fintech---Dashboard-/actions/workflows/ci.yml/badge.svg)

---

## Overview

This project connects to a UK bank account via TrueLayer's Open Banking API, pulls real account and transaction data, categorises spending using rule-based matching, detects recurring subscriptions, and tracks monthly budgets against actual spend — all through a working full-stack application with a live dashboard.

### How Open Banking works here

Open Banking is a UK/EU regulatory framework requiring banks to expose account data to authorised third parties via secure APIs, with the account holder's explicit consent. TrueLayer acts as a unified layer over many UK banks, so this app doesn't need to integrate with each bank individually.

The flow follows the standard **OAuth2 Authorization Code Grant**:

1. The app redirects the user to TrueLayer's consent screen with a `client_id`, `redirect_uri`, and requested `scope`s.
2. The user logs into their bank (or, in Sandbox, a Mock Bank) and approves access.
3. TrueLayer redirects back to the app's `redirect_uri` with a temporary `authorization code`.
4. The backend exchanges that code (with the `client_secret`) for an `access_token` (short-lived) and `refresh_token` (long-lived).
5. The app uses the `access_token` to call TrueLayer's Data API for accounts and transactions, and automatically refreshes it when expired.

---

## Features

- **OAuth2 bank connection** — full TrueLayer consent flow with Mock Bank support in Sandbox
- **Encrypted token storage** — access and refresh tokens encrypted at rest with AES-GCM before being saved
- **Automatic token refresh** — expired access tokens are silently refreshed using the stored refresh token, no re-consent needed
- **Account sync** — pulls connected accounts and live balances from TrueLayer's Data API
- **Transaction sync** — pulls transactions per account, deduplicated by TrueLayer's transaction ID
- **Rule-based categorisation** — matches transactions to categories (Groceries, Dining, Transport, Bills, etc.) via merchant name keyword matching
- **Recurring payment detection** — groups transactions by merchant, detects monthly patterns (~28–32 day intervals with consistent amounts) to flag subscriptions
- **Budgets & alerts** — set a monthly limit per category, track spend-to-date and over-budget status
- **Monthly spend summary** — aggregated spend by category, filterable by month
- **React dashboard** — total balance, spend-by-category pie chart, transaction list, budget progress cards, one-click bank connection
- **CI pipeline** — GitHub Actions running the full test suite (with a real Postgres service container) on every push

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Auth | OAuth2 (TrueLayer), AES-GCM token encryption |
| Frontend | React (Vite), Recharts |
| Infra | Docker, Docker Compose, GitHub Actions CI |
| Testing | JUnit 5, Mockito |

---

## Architecture
┌─────────────┐ REST/JSON ┌──────────────────┐
│ React │ ───────────────────▶│ Spring Boot │
│ (Vite, │◀─────────────────── │ REST API │
│ Recharts) │ │ │
└─────────────┘ └─────────┬─────────┘
│
┌───────────────────┼───────────────────┐
│ │ │
┌─────▼─────┐ ┌───────▼───────┐ ┌──────▼──────┐
│ PostgreSQL │ │ TrueLayer │ │ AES-GCM │
│ (Flyway │ │ Auth + Data │ │ Token │
│ migrated) │ │ API │ │ Encryption │
└────────────┘ └────────────────┘ └─────────────┘


**Request flow for a typical sync:**
`Client → Controller → Service → Repository → Postgres`, with services calling out to TrueLayer's API via Spring's `RestClient` where needed (account sync, transaction sync, token refresh).

### Database schema

- `users` — app users (currently single default user; multi-user auth not yet implemented)
- `user_tokens` — encrypted OAuth tokens per user/provider
- `accounts` — synced bank accounts and balances
- `categories` — spending categories (seeded: Groceries, Dining, Transport, Bills, Entertainment, Shopping, Subscriptions, Income, Other)
- `transactions` — synced transactions, linked to an account and (once categorised) a category
- `budgets` — monthly spend limits per category
- `recurring_payments` — detected subscriptions/recurring charges

Schema is managed entirely through versioned Flyway migrations in `backend/src/main/resources/db/migration/` — Hibernate is set to `validate` only, never `update`, so the schema is always explicit and auditable.

---

## API Endpoints

GET /api/auth/connect → generates TrueLayer consent URL
GET /api/auth/callback → handles OAuth callback, exchanges code for tokens

GET /api/accounts → list connected accounts
POST /api/accounts/sync → sync accounts + balances from TrueLayer

GET /api/transactions → list all transactions
POST /api/transactions/sync → sync transactions from TrueLayer
POST /api/transactions/categorize → run rule-based categorisation on uncategorised transactions
GET /api/transactions/summary → monthly spend by category (optional ?month=YYYY-MM)

GET /api/categories → list categories

GET /api/budgets → list budgets
POST /api/budgets → create/update a budget
GET /api/budgets/alerts → budget status (spend vs limit, over-budget flag)

GET /api/recurring-payments → list detected recurring payments
POST /api/recurring-payments/detect → run recurring payment detection


---

## Setup

### Prerequisites
- JDK 21
- Docker Desktop
- Node.js + npm
- A [TrueLayer Console](https://console.truelayer.com) Sandbox app (free) — for `client_id` and `client_secret`

### 1. Clone the repo

```bash
git clone https://github.com/Ali627miya/Fintech---Dashboard-.git
cd Fintech---Dashboard-
```

### 2. Start Postgres

```bash
cd backend
docker compose up -d
```

### 3. Configure the backend

Create `backend/src/main/resources/application.yml` (this file is intentionally **not** committed to git — it holds real secrets):

```yaml
spring:
  application:
    name: fintech-dashboard
  datasource:
    url: jdbc:postgresql://localhost:5432/fintech_dashboard
    username: fintech_user
    password: local_dev_password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

truelayer:
  client-id: YOUR_TRUELAYER_SANDBOX_CLIENT_ID
  client-secret: YOUR_TRUELAYER_SANDBOX_CLIENT_SECRET
  redirect-uri: http://localhost:8080/api/auth/callback
  auth-base-url: https://auth.truelayer-sandbox.com
  api-base-url: https://api.truelayer-sandbox.com

app:
  encryption-key: A_BASE64_ENCODED_32_BYTE_KEY   # generate with: openssl rand -base64 32

server:
  port: 8080
```

Register `http://localhost:8080/api/auth/callback` as a redirect URI in your TrueLayer Console app settings.

### 4. Run the backend

```bash
./mvnw spring-boot:run
```

Verify it's up:
```bash
curl http://localhost:8080/api/categories
```

### 5. Run the frontend

```bash
cd ../frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

### 6. Connect a bank account

Click **Connect Bank Account** on the dashboard, or manually:
```bash
curl http://localhost:8080/api/auth/connect
```
Open the returned URL in a browser, approve access via TrueLayer's Mock Bank, then sync:
```bash
curl -X POST http://localhost:8080/api/accounts/sync
curl -X POST http://localhost:8080/api/transactions/sync
curl -X POST http://localhost:8080/api/transactions/categorize
curl -X POST http://localhost:8080/api/recurring-payments/detect
```

---

## Testing

```bash
cd backend
./mvnw test
```

Tests run automatically on every push via GitHub Actions, against a real Postgres service container (see `.github/workflows/ci.yml`).

---

## Key Design Decisions

- **Flyway over Hibernate `ddl-auto: update`** — explicit, versioned SQL migrations are safer and more auditable than letting Hibernate auto-generate schema changes.
- **Tokens encrypted at rest (AES-GCM)** — OAuth tokens are treated as sensitive credentials, not plain data, even in a sandbox environment.
- **`application.yml` is git-ignored** — real secrets never touch version control; a safe template with dummy values lives at `backend/src/test/resources/application.yml` for CI.
- **Rule-based categorisation over ML** — a pragmatic first pass; the natural next step would be learning from the user's manual re-categorisations over time.
- **DTOs at every API boundary** — controllers never return JPA entities directly, keeping the persistence layer decoupled from the API contract.

---

## What I'd Add With More Time

- Real multi-user authentication (currently single default user)
- Anomaly detection for unusual transactions
- End-of-month balance forecasting based on recurring payments + spending velocity
- Testcontainers-based integration tests alongside the current unit tests
- Dockerfile + full container deployment for the backend
- Deployed live demo (Render/Railway + Vercel)

---

## License

MIT
