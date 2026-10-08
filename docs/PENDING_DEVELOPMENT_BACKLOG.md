# 📋 SariTrack - Pending Development Backlog & Technical Gap Analysis

> **Target Audience:** Developers & AI Coding Assistants (Antigravity, Claude, Copilot)  
> **Repository:** IT342-Tapales-SariTrack  
> **Last Updated:** October 2026  
> **Status:** Active Backlog  

---

## 🎯 Executive Overview

This document catalogs technical debt, security improvements, missing business workflows, and cross-tier parity gaps identified during the system audit of **SariTrack** (Backend, React Web, and Android Mobile). 

Any developer or AI assistant working on this repository should refer to this document prior to implementing new capabilities or refactoring existing features.

---

## 🚦 Priority Matrix

| ID | Category | Gap / Feature Description | Affected Tier | Severity | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **GAP-01** | Security / Auth | RBAC Role Enforcement on Platform Admin Endpoints | Backend | 🔴 High | ✅ Resolved |
| **GAP-02** | Multi-Tenancy | Scope Customer Debt History Query by Vendor ID | Backend | 🔴 High | ✅ Resolved |
| **GAP-03** | Payments | Dynamic Frontend Redirect URLs for PayMongo Checkout | Backend | 🟡 Medium | ✅ Resolved |
| **GAP-04** | POS / Inventory | Order Cancellation & Voiding (Stock & Debt Reversal) | Backend & Web/Mobile | 🟡 Medium | ✅ Resolved |
| **GAP-05** | Authentication | Password Recovery / Forgot Password Email Flow | Backend & Web/Mobile | 🟡 Medium | ✅ Resolved |
| **GAP-06** | API Design | Standardized HTTP Error Responses on User Registration | Backend & Web | 🟢 Low | ✅ Resolved |
| **GAP-07** | Feature Parity | Mobile Notification Center UI & Synchronization | Mobile (Android) | 🟡 Medium | ✅ Resolved |
| **GAP-08** | Feature Parity | Mobile PDF Receipt & Ledger Statement Export | Mobile (Android) | 🟢 Low | ⏳ Pending |

---

## 🔍 Detailed Gap Specifications & Implementation Blueprints

---

### GAP-01: RBAC Role Enforcement on Platform Admin Endpoints (✅ Resolved)
* **Severity:** 🔴 High (Security Vulnerability)
* **Status:** Resolved in `feat/admin-rbac-gap01`.
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/core/security/JwtFilter.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/core/security/JwtFilter.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/core/security/JwtUtils.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/core/security/JwtUtils.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/core/security/SecurityConfig.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/core/security/SecurityConfig.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/admin/controller/AdminController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/admin/controller/AdminController.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/core/exception/GlobalExceptionHandler.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/core/exception/GlobalExceptionHandler.java)
  * [`backend/src/test/java/edu/cit/tapales/saritrack/feature/admin/controller/AdminControllerSecurityTest.java`](file:///backend/src/test/java/edu/cit/tapales/saritrack/feature/admin/controller/AdminControllerSecurityTest.java)
* **Problem Statement:**
  * Endpoints `/api/admin/stats` and `/api/admin/vendors/analytics` expose platform-wide sales volume, revenue, and store analytics.
  * In `JwtFilter.java`, `UsernamePasswordAuthenticationToken` was instantiated with `new ArrayList<>()` for authorities. Consequently, no roles (`ROLE_ADMIN`, `ROLE_VENDOR`) existed in Spring Security's `SecurityContext`.
  * `AdminController.java` lacked `@PreAuthorize("hasRole('ADMIN')")` or role verification. Any authenticated user holding a vendor JWT could query all other vendors' financial data.
* **Resolution Details:**
  1. Updated `JwtUtils.java` to embed `role` claim in JWT tokens and expose `extractRole(token)`.
  2. Updated `JwtFilter.java` to map extracted or database role to `SimpleGrantedAuthority("ROLE_" + role.toUpperCase())` and register with `SecurityContextHolder`.
  3. Enabled `@EnableMethodSecurity` in `SecurityConfig.java` and restricted `/api/admin/**` to `hasRole('ADMIN')`.
  4. Annotated `AdminController.java` endpoints with `@PreAuthorize("hasRole('ADMIN')")`.
  5. Corrected `GlobalExceptionHandler.java` to handle Spring Security's `AccessDeniedException` and return structured HTTP 403 Forbidden.
  6. Added `AdminControllerSecurityTest.java` verifying 200 OK for ADMIN, 403 Forbidden for VENDOR, and 401 Unauthorized for unauthenticated requests, with comprehensive unit test coverage in `JwtFilterTest`, `JwtUtilsTest`, and `AuthControllerTest`.

---

### GAP-02: Scope Customer Debt History Query by Vendor ID (✅ Resolved)
* **Severity:** 🔴 High (Multi-Tenant Data Leak)
* **Status:** Resolved in `fix/order-tenant-scoping-gap02`.
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderHistoryController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderHistoryController.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/repository/OrderRepository.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/repository/OrderRepository.java)
  * [`backend/src/test/java/edu/cit/tapales/saritrack/feature/order/controller/OrderHistoryControllerTest.java`](file:///backend/src/test/java/edu/cit/tapales/saritrack/feature/order/controller/OrderHistoryControllerTest.java)
* **Problem Statement:**
  * When `customerId` is passed to `/api/orders/history?vendorId=...&customerId=...`, the controller runs `orderRepository.findAll().stream().filter(...)`.
  * This executed a full table scan and discarded the `vendorId` filter, leaking cross-vendor customer orders.
* **Resolution Details:**
  1. Added repository method `List<Order> findByCustomerIdAndVendorId(Long customerId, Long vendorId)`.
  2. Updated `OrderHistoryController` to query database directly scoped to both parameters.
  3. Added unit tests verifying tenant-isolated filtering and zero results for cross-tenant IDs.

---

### GAP-03: Dynamic Frontend Redirect URLs for PayMongo Checkout (✅ Resolved)
* **Severity:** 🟡 Medium (Operational / Cloud Deployment Issue)
* **Status:** Resolved in `fix/payment-dynamic-redirect-gap03`.
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/payment/controller/PaymentController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/payment/controller/PaymentController.java)
  * [`backend/src/main/resources/application.properties`](file:///backend/src/main/resources/application.properties)
  * [`backend/src/test/java/edu/cit/tapales/saritrack/feature/payment/controller/PaymentControllerTest.java`](file:///backend/src/test/java/edu/cit/tapales/saritrack/feature/payment/controller/PaymentControllerTest.java)
* **Problem Statement:**
  * `successUrl` and `cancelUrl` were hardcoded to `http://localhost:5173`. In production or mobile testing, PayMongo redirected to an unreachable page.
* **Resolution Details:**
  1. Configured `app.frontend.url=${FRONTEND_URL:http://localhost:5173}` in `application.properties`.
  2. Injected `frontendUrl` into `PaymentController` and added support for dynamic `redirectUrl` overrides in checkout payloads.
  3. Added unit tests verifying dynamic URL formatting for both default property fallback and custom redirect URLs.

---

### GAP-04: Order Cancellation & Voiding Mechanism (POS Mistake Handling) (✅ Resolved)
* **Severity:** 🟡 Medium (Core Business Workflow)
* **Status:** Resolved in `feat/order-cancellation-gap04`.
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/service/OrderService.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/service/OrderService.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderController.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/core/exception/GlobalExceptionHandler.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/core/exception/GlobalExceptionHandler.java)
  * [`web/src/features/transactions/Transactions.jsx`](file:///web/src/features/transactions/Transactions.jsx)
  * [`backend/src/test/java/edu/cit/tapales/saritrack/feature/order/service/OrderServiceTest.java`](file:///backend/src/test/java/edu/cit/tapales/saritrack/feature/order/service/OrderServiceTest.java)
  * [`backend/src/test/java/edu/cit/tapales/saritrack/feature/order/controller/OrderControllerTest.java`](file:///backend/src/test/java/edu/cit/tapales/saritrack/feature/order/controller/OrderControllerTest.java)
  * [`web/src/features/transactions/Transactions.test.jsx`](file:///web/src/features/transactions/Transactions.test.jsx)
* **Problem Statement:**
  * Orders had status `CANCELLED` defined in the domain, but there was no API endpoint or UI action to void an order.
  * When a sale was completed, stock was decremented immediately and credit sales incremented customer `currentDebt`.
  * If a cashier entered an incorrect transaction or a customer canceled an item, the store owner could not reverse it without manual DB intervention.
* **Resolution Details:**
  1. Implemented `@Transactional Order cancelOrder(Long orderId, Long vendorId, String reason)` in `OrderService.java` that enforces vendor ownership, verifies non-cancelled status, automatically replenishes product stock, reverts customer debts if a debt order was voided, updates status to `CANCELLED`, and dispatches an audit notification.
  2. Exposed `POST /api/orders/{id}/cancel` in `OrderController.java` supporting vendor ID via request parameter or request payload with optional audit reason.
  3. Added `IllegalStateException` handling to `GlobalExceptionHandler.java` mapping to HTTP 400 Bad Request.
  4. Added inline table and modal "Void Order" triggers with reason confirmation in `Transactions.jsx`.
  5. Added comprehensive automated tests in `OrderServiceTest`, `OrderControllerTest`, and `Transactions.test.jsx` verifying 100% pass rate.

---

### GAP-05: Password Recovery / Forgot Password Email Flow
* **Severity:** 🟡 Medium (User Management) - ✅ **Resolved**
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/entity/User.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/entity/User.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/repository/UserRepository.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/repository/UserRepository.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/notification/service/EmailService.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/notification/service/EmailService.java)
  * [`web/src/features/auth/Login.jsx`](file:///web/src/features/auth/Login.jsx)
  * [`web/src/features/auth/Login.test.jsx`](file:///web/src/features/auth/Login.test.jsx)
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/auth/LoginActivity.kt`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/auth/LoginActivity.kt)
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/auth/AuthApiService.kt`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/auth/AuthApiService.kt)
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/auth/AuthModels.kt`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/auth/AuthModels.kt)
* **Problem Statement:**
  * Authentication previously only provided register and login.
  * If a vendor forgot their password, they could not regain access without direct database edits.
* **Resolution:**
  1. Added `resetToken` and `resetTokenExpiry` fields to `User` entity and `findByResetToken` query method in `UserRepository`.
  2. Implemented `POST /api/auth/forgot-password`: generates a secure UUID token with 15-minute expiry and sends a branded HTML email via `EmailService`.
  3. Implemented `POST /api/auth/reset-password`: validates token existence and expiry, hashes new password with `BCryptPasswordEncoder`, and clears token.
  4. Added "Forgot password?" modal on Web `Login.jsx` (with request token and reset password steps, plus URL query param detection) and interactive dialog on Mobile `LoginActivity.kt`.
  5. Verified 100% test pass rate across backend (165 tests), web (105 tests), and mobile (114 tests).

---

### GAP-06: Standardized HTTP Error Responses on User Registration
* **Severity:** 🟢 Low (API Quality & Clean Architecture) - ✅ **Resolved**
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java)
  * [`backend/src/test/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthControllerTest.java`](file:///backend/src/test/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthControllerTest.java)
  * [`web/src/features/auth/Register.jsx`](file:///web/src/features/auth/Register.jsx)
* **Problem Statement:**
  * `AuthController.register(...)` returned a plain string: `"Error: Email already exists!"` with HTTP `200 OK`.
  * Standard REST convention requires HTTP `409 Conflict` with a structured JSON body `{"error": "Email already exists!"}` and `200 OK` with `{"message": "User registered successfully!"}`.
* **Resolution:**
  1. Updated `register` return type to `ResponseEntity<?>` returning HTTP `409 Conflict` with `{"error": "Email already exists!"}` and HTTP `200 OK` with structured JSON on success.
  2. Updated Web `Register.jsx` catch block to extract error messages from Axios responses.
  3. Verified unit tests in `AuthControllerTest` and `Register.test.jsx`.

---

### GAP-07: Mobile In-App Notification Center (✅ Resolved)
* **Severity:** 🟡 Medium (Feature Parity)
* **Status:** Resolved in `feat/notification-mobile-center-gap07`.
* **Affected Files:**
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/notification/`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/notification/)
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/core/api/RetrofitClient.kt`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/core/api/RetrofitClient.kt)
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/dashboard/HomeFragment.kt`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/dashboard/HomeFragment.kt)
  * [`mobile/app/src/main/res/layout/fragment_home.xml`](file:///mobile/app/src/main/res/layout/fragment_home.xml)
  * [`mobile/app/src/main/res/layout/bottom_sheet_notifications.xml`](file:///mobile/app/src/main/res/layout/bottom_sheet_notifications.xml)
  * [`mobile/app/src/main/res/layout/item_notification.xml`](file:///mobile/app/src/main/res/layout/item_notification.xml)
  * [`mobile/app/src/test/java/edu/cit/tapales/saritrack/feature/notification/NotificationModelsTest.kt`](file:///mobile/app/src/test/java/edu/cit/tapales/saritrack/feature/notification/NotificationModelsTest.kt)
* **Problem Statement:**
  * The React web client has a live notification bell dropdown displaying low-stock alerts, utang additions, and digital payment confirmations.
  * The Android mobile app had no notification interface, leaving mobile-only cashiers uninformed about backend alerts.
* **Resolution Details:**
  1. Implemented `NotificationModels.kt` and `NotificationApiService.kt` mapping `/api/notifications?vendorId=...`, `/api/notifications/sync?vendorId=...`, `/api/notifications/{id}/read`, and `/api/notifications/read-all?vendorId=...`.
  2. Created `NotificationBottomSheet.kt` with a RecyclerView, pull-to-refresh (`SwipeRefreshLayout`), mark individual read on click, and "Mark all as read" batch action.
  3. Added bell icon button and live unread badge counter in `HomeFragment.kt` header adjacent to logout, automatically refreshing on screen load and on dismiss.
  4. Created unit tests in `NotificationModelsTest.kt` verifying model construction, timestamp parsing, and default flags.
  5. Verified 100% test pass rate across backend (165 tests), web (105 tests), and mobile (120 tests).

---

### GAP-08: Mobile PDF Receipt & Statement Generation
* **Severity:** 🟢 Low (Feature Parity)
* **Affected Files:**
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/transaction/`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/transaction/)
* **Problem Statement:**
  * Web has instant PDF and CSV generation for transaction histories and customer debt statements via `jspdf` and `jspdf-autotable`.
  * Mobile currently lacks native document export or Android Share Sheet integration for digital receipts.
* **Implementation Blueprint:**
  1. Use Android's `PdfDocument` API or HTML-to-PDF print adapter to render a receipt layout.
  2. Provide a "Share Receipt via SMS / Messenger / Print" action button upon sale completion.

---

## 🛡️ Development & Testing Rules (Mandatory Gate)

Before integrating or pushing any fix for items in this backlog:
1. **Isolated Sub-Branching:** Never write code or commit directly on `main`. Always create a dedicated sub-branch: `feat/<slice>-...` or `fix/<slice>-...`.
2. **Mandatory Test Verification Gate (100% Pass Required):**
   * Backend: `cd backend && .\mvnw.cmd test` (143/143 must pass)
   * Web: `cd web && npx vitest run` (102/102 must pass)
   * Mobile: `cd mobile && .\gradlew.bat testDebugUnitTest` (112/112 must pass)
3. **Local Merge & Remote Push Protocol:** Only after 100% verification that changes are stable and predictable in the sub-branch, merge into local `main`. Local `main` is the only branch pushed to remote repository (`origin`).
4. **Maintain Invariants:** Every query affecting products, sales, customers, or stock **must filter by `vendor_id`**. Zero hardcoded credentials.
