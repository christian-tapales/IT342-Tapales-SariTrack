# AGENTS.md - SariTrack Developer & AI Assistant Instructions

Welcome to the **SariTrack** repository (IT342 - System Integration and Architecture Final Project).

## 🚀 Quick Orientation

- **Project:** SariTrack - Multi-Tenant SaaS Retail POS and Digital Debt Ledger ("Listahan").
- **Tech Stack:**
  - **Backend:** Java 17, Spring Boot 3, Spring Security (JWT + Google OAuth 2.0), PostgreSQL (Supabase), PayMongo Sandbox, Gmail SMTP, ExchangeRate API.
  - **Web Frontend:** React 18, Vite, Tailwind CSS, Vitest.
  - **Mobile:** Android Kotlin (API 34+), Retrofit 2, ML Kit (Barcode Scanner), Glide, EncryptedSharedPreferences.
- **Architecture:** Vertical Slice Architecture (VSA) organized into features across all tiers.
- **Detailed Technical Context:** Always read [`docs/PROJECT_CONTEXT.md`](file:///docs/PROJECT_CONTEXT.md) for complete architecture, slice mapping, credentials structure, and workflow gotchas.

## 🧪 Testing Guidelines (Never Skip)

Always ensure tests pass before proposing or committing code changes:
- **Backend (143 tests):** Run `cd backend && ./mvnw test` (Uses in-memory H2 DB defined in `backend/src/test/resources/application.properties`).
- **Web (102 tests):** Run `cd web && npx vitest run` (Must use `run` mode to prevent hanging interactive watch mode). When mocking API calls, mock `../../core/api/api` rather than `axios` directly.
- **Mobile (112 tests):** Run `cd mobile && .\gradlew.bat testDebugUnitTest`.

## 🛡️ Coding & Architectural Standards

1. **Maintain Vertical Slices:** Keep feature-specific logic within `feature/<slice_name>` (controllers, services, repositories, and models).
2. **Security & Secrets:** Never commit real secrets. Use `application-local.properties` (ignored by Git) or environment variables for credentials.
3. **Multi-Tenancy:** Ensure database operations respect vendor boundaries (`vendor_id`).
