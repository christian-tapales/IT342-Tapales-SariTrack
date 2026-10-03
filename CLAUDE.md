# CLAUDE.md - SariTrack Assistant Guidelines

This repository enforces strict contribution and testing standards.

## 📖 Primary Guidelines
- **Universal Instructions & Protocol:** Read [`AGENTS.md`](./AGENTS.md) for the mandatory 6-step lifecycle protocol.
- **Deep Technical Reference:** Read [`docs/PROJECT_CONTEXT.md`](./docs/PROJECT_CONTEXT.md) for full architecture and external service integration details.

## 🧪 Quick Test Commands
- **Backend:** `cd backend && ./mvnw test`
- **Web:** `cd web && npx vitest run` (Must use `run` mode)
- **Mobile:** `cd mobile && .\gradlew.bat testDebugUnitTest`

## 🛡️ Coding Rules
- Follow Vertical Slice Architecture (`feature/<slice>`).
- Enforce multi-tenancy (`vendor_id`).
- Never commit secrets.
- Use Conventional Commits: `<type>(<scope>): <subject>`.
