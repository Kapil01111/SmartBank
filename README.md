# SmartBank – AI-Powered Banking Management System

A full-stack demo banking application with a Spring Boot REST API, React dashboard, MySQL persistence, JWT authentication, role-based authorization, banking transactions, and an AI financial-insights endpoint.

> \\\*\\\*Important:\\\*\\\* This is an educational portfolio project, not production banking software. Do not use real customer data or real funds. Before production use, add professional security review, audit controls, fraud controls, secrets management, rate limiting, monitoring, backups, and regulatory compliance.

## Stack

* Java 25, Spring Boot 3.5.x, Spring Security, Spring Data JPA, Hibernate
* MySQL 8, Maven
* JWT (JJWT), Bean Validation
* React + Vite
* OpenAI-compatible Chat Completions API integration (configurable base URL/model)

## Quick start

### 1\. Requirements

* JDK 25
* Maven 3.9+
* MySQL 8+
* Node.js 20+
* An AI API key (optional; the rest of the app works without it)

### 2\. Database

Create a MySQL database:

```sql
CREATE DATABASE smartbank;
```

### 3\. Backend configuration

Copy `backend/.env.example` values into your environment, or edit `backend/src/main/resources/application.yml`.
Set:

* `DB\\\_USERNAME`, `DB\\\_PASSWORD`
* `JWT\\\_SECRET` to a random Base64 secret of at least 32 bytes
* `AI\\\_API\\\_KEY` if you want live AI insights

Example PowerShell:

```powershell
$env:DB\\\_USERNAME="root"
$env:DB\\\_PASSWORD="your\\\_mysql\\\_password"
$env:JWT\_SECRET="YOUR\_GENERATED\_SECRET"
$env:AI\\\_API\\\_KEY="your\\\_api\\\_key"
cd backend
mvn spring-boot:run
```

Backend: `http://localhost:8080`

A development default JWT secret is included so the project starts locally; replace it before sharing/deploying.

### 4\. Frontend

```powershell
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:5173`

Vite proxies `/api` to the backend. Register a user, log in, create an account, then try deposits, withdrawals and transfers.

## Demo API

All paths are relative to `http://localhost:8080`.

|Method|Endpoint|Auth|Purpose|
|-|-|-|-|
|POST|`/api/auth/register`|No|Register customer|
|POST|`/api/auth/login`|No|Login and receive JWT|
|GET|`/api/customers/me`|JWT|Current profile|
|POST|`/api/accounts`|JWT|Open account|
|GET|`/api/accounts`|JWT|List own accounts|
|GET|`/api/accounts/{id}`|JWT|Account details|
|POST|`/api/accounts/{id}/deposit`|JWT|Deposit|
|POST|`/api/accounts/{id}/withdraw`|JWT|Withdraw|
|POST|`/api/transfers`|JWT|Transfer between own accounts|
|GET|`/api/transactions`|JWT|Own transaction history|
|POST|`/api/ai/insights`|JWT|AI-generated spending insights|

### Sample requests

Register:

```json
{
  "fullName": "Aarav Sharma",
  "email": "aarav@example.com",
  "password": "StrongPass123!"
}
```

Login:

```json
{
  "email": "aarav@example.com",
  "password": "StrongPass123!"
}
```

Open account:

```json
{
  "accountType": "SAVINGS"
}
```

Deposit / withdraw:

```json
{
  "amount": 2500.00,
  "description": "Initial deposit"
}
```

Transfer:

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00,
  "description": "Monthly savings"
}
```

## AI integration

The AI endpoint summarizes the authenticated user's transaction data and asks the configured OpenAI-compatible API for general budgeting observations. Set `AI\\\_API\\\_KEY`; optionally configure `AI\\\_BASE\\\_URL` and `AI\\\_MODEL`. If no key is configured, the endpoint returns a helpful configuration message rather than failing the banking application.

Never send account credentials, JWTs, card numbers, or sensitive identifiers to an AI provider. This demo sends aggregated transaction descriptions/categories and amounts; review provider data policies before using real data.

## Security and implementation notes

* BCrypt password hashing; JWT bearer authentication.
* Customers can only access their own accounts and transactions.
* Money values use `BigDecimal`, not floating point.
* Banking mutations use database transactions and pessimistic row locking.
* Transfer rejects same-account, non-positive, insufficient-funds, and unauthorized-account requests.
* The app is intentionally simplified: no card processing, external payment rails, KYC, OTP/MFA, ledger reconciliation, or real bank integration.
* `ddl-auto: update` is convenient for local learning; use Flyway/Liquibase migrations in serious deployments.

## Postman

Import `postman/SmartBank.postman\\\_collection.json`. Set collection variables:

* `baseUrl` = `http://localhost:8080`
* `token` = JWT returned by login
* `accountId`, `toAccountId` = IDs from account creation

## Build

Backend:

```bash
cd backend \\\&\\\& mvn clean package
```

Frontend:

```bash
cd frontend \\\&\\\& npm run build
```

