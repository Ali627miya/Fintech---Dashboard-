# Personal Finance Dashboard

Open Banking finance dashboard built with Spring Boot + React, using TrueLayer's UK Open Banking sandbox API.

A personal finance dashboard that connects to a UK bank account via TrueLayer’s
Open Banking API, pulls transaction data, categorises spending, and (eventually) tracks budgets and detects
recurring payments/anomalies .

Open Banking is a UK/EU regulatory framework requiring banks to expose account data to authorised
third parties via secure APIs, with the account holder’s explicit consent. TrueLayer is a middleman that
implements this for many UK banks behind one unified API, so you don’t have to integrate with each bank
separately.
The flow follows standard OAuth2 Authorization Code Grant:
1. Your app redirects the user to TrueLayer’s consent screen with your client_id, a redirect_uri,
and requested scopes (what data you want access to).
2. The user logs into their bank (or, in Sandbox, a Mock Bank) and approves access.
3. TrueLayer redirects the user back to your redirect_uri with a temporary authorization code.
4. Your backend exchanges that code (plus your client_secret, proving your app’s identity) for an
access_token (short-lived) and a refresh_token (long-lived, used to get new access tokens later
without the user logging in again).
5. Your app uses the access_token to call TrueLayer’s Data API for accounts/transactions.

## Features

Key commands used:
git clone https://github.com/ALi627miya/fintech-dashboard.git
cd fintech-dashboard
git add .gitignore
git commit -m "Update gitignore for secrets, IDE, and frontend files"
git push
git checkout -b feature/initial-scaffold
git add .
git commit -m "..."
git push -u origin feature/initial-scaffold

## Tech Stack
- Backend: Java, Spring Boot, PostgreSQL, Flyway
- Frontend: React
- Infra: Docker, GitHub Actions CI

## Setup3.5 First Flyway Migration (V1__init_schema.sql)
Created the full initial schema: users, user_tokens, accounts, categories, transactions, budgets,
recurring_payments, plus seeded 9 default categories (Groceries, Dining, Transport, Bills, Entertainment,
Shopping, Subscriptions, Income, Other).
File location (important – Flyway requires this exact path):
src/main/resources/db/migration/V1__init_schema.sql

## Architecture


7. Full Command Reference (chronological, for your own notes)
# --- GitHub setup ---
git clone https://github.com/YOUR_USERNAME/fintech-dashboard.git
cd fintech-dashboard
git add .gitignore && git commit -m "Update gitignore" && git push
# --- Branching workflow ---
git checkout -b feature/initial-scaffold
git add . && git commit -m "..." && git push -u origin feature/initial-scaffold
git checkout -b feature/oauth-flow
# --- Maven wrapper fixes ---
chmod +x mvnw
mvn -N wrapper:wrapper
# --- Docker / Postgres ---
docker compose up -d
docker ps
docker compose down -v
# --- Running the app ---
./mvnw spring-boot:run
# --- Testing endpoints ---
curl -i http://localhost:8080/api/categories
curl http://localhost:8080/api/auth/connect
# --- Debugging ---
find . -name "SomeFile.java"
pwd
8
