# 🧠 SariTrack Master Project Context & AI Reference Guide

> **Target Audience:** Developers & Agentic Coding Assistants (Antigravity, Claude, Copilot)  
> **System Name:** SariTrack (Multi-Tenant SaaS Retail & Credit Management System)  
> **Course Context:** IT342 - System Integration and Architecture (Final Project)  
> **Author:** Christian Kyle Bayarcal Tapales  
> **Last Updated:** October 2026  

---

## 1. 🏗️ Architecture & High-Level Design

SariTrack is structured as a **Three-Tier Architecture** organized with a **Vertical Slice Architecture (VSA)** across all tiers.

```
IT342-Tapales-SariTrack/
├── backend/                  # Java 17 + Spring Boot 3 REST API
│   ├── src/main/java/edu/cit/tapales/saritrack/
│   │   ├── core/             # Cross-cutting: Security, JWT, Global Exceptions, System Health
│   │   └── feature/          # Vertical Slices: auth, product, order, notification, payment, customer, admin, vendor
│   └── src/test/resources/   # In-memory H2 DB config for zero-network automated tests
├── web/                      # React 18 + Vite + Tailwind CSS + Vitest
│   ├── src/core/             # Shared API client (dynamic baseURL), Layouts, Reusable UI inputs
│   └── src/features/         # Vertical Slices: auth, dashboard, inventory, pos, listahan, transactions, admin, payment
└── mobile/                   # Android Kotlin (API 34+) + MVVM + Retrofit 2 + ML Kit
    └── app/src/main/java/edu/cit/tapales/saritrack/
        ├── core/             # API client, Session Manager (EncryptedSharedPreferences), Utilities
        └── feature/          # Feature packages: auth, pos, inventory, customer, payment, transaction, dashboard
```

---

## 2. 🔌 External Integrations & Cloud Services

| Integration | Technology | Purpose & Implementation Details |
| :--- | :--- | :--- |
| **Database** | PostgreSQL on Supabase | Multi-tenant schema with tenant/vendor isolation. Connection pooler at port `6543`. |
| **Image CDN** | Supabase Storage (`products` bucket) | Direct image upload & CDN URL storage for product catalogs. |
| **Payments** | PayMongo Sandbox API | Hosted checkout links, e-wallet (GCash), card processing, and cryptographic webhook listener (`whsk_*`). |
| **Identity** | Google OAuth 2.0 & JWT | Backend Spring Security token verification with local JWT generation for stateless mobile & web auth. |
| **Notifications** | Gmail SMTP Gateway | Automated low-stock and transaction settlement email alerts. |
| **Currency** | ExchangeRate API | Dynamic exchange rate conversion with local fallback rates. |
| **Barcode Scan** | Google ML Kit + Open Barcode API | High-speed on-device camera scanner on Android with auto product title lookup. |

---

## 3. 🧪 Testing Commands & Guidelines

Full suite has **357 automated tests** (143 Backend, 102 Web, 112 Mobile) with 100% pass rate.

### Backend (Java / JUnit 5 / Mockito)
* **Directory:** `backend/`
* **Run All Tests:** `./mvnw test` (or `.\mvnw.cmd test` on Windows)
* **Run Single Test:** `./mvnw test -Dtest=ClassName`
* **Test Isolation:** Uses in-memory H2 database defined in `src/test/resources/application.properties` to ensure tests execute without external Supabase network dependencies.

### Web Frontend (React / Vitest / RTL)
* **Directory:** `web/`
* **Run All Tests (Single Run):** `npx vitest run` (Do NOT run `npm test` without `--run` or it enters interactive watch mode).
* **Run Single Test File:** `npx vitest run src/features/auth/Register.test.jsx`
* **Mocking Tip:** `src/core/api/api.js` is the standard axios wrapper. When mocking API calls in tests, mock `../../core/api/api` rather than `axios` directly.

### Mobile App (Android / Kotlin JUnit)
* **Directory:** `mobile/`
* **Run Unit Tests:** `.\gradlew.bat testDebugUnitTest`
* **Configuration:** Android API Level 34+, Kotlin Gradle DSL (`build.gradle.kts`).

---

## 4. ⚙️ Running the Applications Locally

Always launch services in this order:
1. **Backend:** `cd backend && ./mvnw spring-boot:run` (Runs on `http://localhost:8080`)
2. **Tunnel (Optional for Mobile/Webhooks):** `ngrok http 8080`
3. **Web Dashboard:** `cd web && npm run dev` (Runs on `http://localhost:5173`)
4. **Mobile POS:** Open `mobile/` in Android Studio. Ensure `BASE_URL` in `RetrofitClient.kt` points to:
   - `http://10.0.2.2:8080/` (Android Emulator)
   - `http://<your-lan-ip>:8080/` (Physical device on same Wi-Fi)
   - `https://<ngrok-tunnel>.ngrok-free.dev/` (Physical device over cellular / webhook testing)

---

## 5. 🛡️ Critical Invariants & Gotchas

1. **Environment Secrets:** Never commit raw API secrets. `application-local.properties` is supported via `spring.config.import=optional:classpath:application-local.properties` and ignored in `.gitignore`.
2. **CORS & Cloud Deployment:** Backend has dynamic `PORT` support (`server.port=${PORT:8080}`) and multi-stage Dockerfile (`backend/Dockerfile`).
3. **Web SPA Routing:** `web/vercel.json` contains rewrites (`{"source": "/(.*)", "destination": "/"}`) to support client-side routing on Vercel without 404s.
4. **Offline Mobile Capabilities:**
   - Auth: Auto-login bypass uses `EncryptedSharedPreferences`.
   - Images: Local disk caching handled by Glide.
   - Analytics: Cached using GSON serialization in `SharedPreferences`.
