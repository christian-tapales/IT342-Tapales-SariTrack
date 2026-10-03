# GitHub Copilot Instructions - SariTrack

These instructions apply to GitHub Copilot in VS Code, JetBrains, and GitHub PR reviews.

## Master Reference
Refer to [`AGENTS.md`](../AGENTS.md) and [`docs/PROJECT_CONTEXT.md`](../docs/PROJECT_CONTEXT.md) for full system architecture, vertical slice breakdown, and testing requirements.

## Core Rules
1. **Vertical Slices:** Keep all feature code grouped by feature slices (`feature/<slice>`).
2. **Multi-Tenancy:** Restrict all database interactions by `vendor_id`.
3. **No Secrets:** Never suggest or commit secrets.
4. **Mandatory Testing:**
   - Backend: `./mvnw test`
   - Web: `npx vitest run`
   - Mobile: `.\gradlew.bat testDebugUnitTest`
5. **Conventional Commits:** `<type>(<scope>): <subject>`
