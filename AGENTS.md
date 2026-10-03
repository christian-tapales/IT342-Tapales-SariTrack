# AGENTS.md - SariTrack Developer & AI Assistant Instructions

Welcome to the **SariTrack** repository (IT342 - System Integration and Architecture Final Project).

---

## 🚀 Quick Orientation

- **Project:** SariTrack - Multi-Tenant SaaS Retail POS and Digital Debt Ledger ("Listahan").
- **Tech Stack:**
  - **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Spring Security (JWT + Google OAuth 2.0), PostgreSQL (Supabase), PayMongo Sandbox, Gmail SMTP, ExchangeRate API.
  - **Web Frontend:** React 18, Vite, Tailwind CSS, Lucide Icons, Recharts, Vitest.
  - **Mobile:** Android Kotlin (API 34+), Retrofit 2, ML Kit (Barcode Scanner), Glide, EncryptedSharedPreferences.
- **Architecture:** Vertical Slice Architecture (VSA) organized into features across all tiers.
- **Detailed Technical Context:** Read [`docs/PROJECT_CONTEXT.md`](file:///docs/PROJECT_CONTEXT.md) for complete architecture, slice mapping, credentials structure, and workflow gotchas.

---

## 🛡️ Mandatory AI Contribution & Git Lifecycle Protocol

Every AI agent or automated developer modifying this repository **MUST** adhere to this 6-step lifecycle:

### Step 1: Context Gathering (Read First, Code Second)
- Always inspect [`AGENTS.md`](file:///AGENTS.md) and [`docs/PROJECT_CONTEXT.md`](file:///docs/PROJECT_CONTEXT.md) before writing or refactoring code.
- Locate the target Vertical Slice (`feature/<slice_name>`) and review existing patterns and dependencies before proposing changes.

### Step 2: Safe Branching Protocol
- **Rule:** **NEVER commit or push directly to `main`.**
- Always create a dedicated branch from the latest `main`:
  - `feat/<slice>-<description>` (new capabilities)
  - `fix/<slice>-<description>` (bug fixes)
  - `test/<slice>-<description>` (automated test suites)
  - `chore/<description>` (tooling, dependencies, configs)
  - `docs/<description>` (documentation updates)

### Step 3: Architectural & Security Invariants
1. **Vertical Slice Isolation:** Feature code must remain within `feature/<slice>` (controllers, services, entities, DTOs). Do not introduce cross-slice circular coupling.
2. **Multi-Tenancy:** Every database query affecting products, sales, customers, or stock **must filter by `vendor_id`**.
3. **Zero Hardcoded Secrets:** Never commit real secrets. Use environment variables or `application-local.properties` (ignored by Git).

### Step 4: Mandatory Test Verification Gate (Never Skip)
Before creating any commit, execute the automated test suites for the affected tiers:
- **Backend (143 tests):** Run `cd backend && ./mvnw test` (Uses in-memory H2 DB in `src/test/resources/application.properties`).
- **Web (102 tests):** Run `cd web && npx vitest run` (Must use `run` mode to prevent hanging interactive watch mode). When mocking API calls, mock `../../core/api/api` rather than `axios` directly.
- **Mobile (112 tests):** Run `cd mobile && .\gradlew.bat testDebugUnitTest`.
- **Gate Rule:** 100% of tests must pass with zero failures or errors before proceeding to commit.

### Step 5: Conventional Commits Standard
Commit messages must strictly follow the Conventional Commits specification:
$$\text{<type>}(\text{<scope>}): \text{<imperative subject>}$$

- **Allowed Types:** `feat`, `fix`, `test`, `refactor`, `perf`, `chore`, `docs`, `style`, `ci`, `build`.
- **Examples:**
  - `feat(payment): implement paymongo webhook signature validation`
  - `fix(web): resolve auth register test api client mock`
  - `test(backend): add boundary verification test for negative stock`
  - `chore(ci): configure github actions test workflow`

### Step 6: Push & Pull Request Protocol
- Push solely to your feature branch: `git push origin <branch-name>`.
- Open a Pull Request targeting `main`.
- **Prohibitions:**
  - ❌ Never use `git push --force` on `main`.
  - ❌ Never use `git commit -a` blindly without checking `git status` and `git diff`.
