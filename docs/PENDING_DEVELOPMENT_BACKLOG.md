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

| ID | Category | Gap / Feature Description | Affected Tier | Severity |
| :--- | :--- | :--- | :--- | :--- |
| **GAP-01** | Security / Auth | RBAC Role Enforcement on Platform Admin Endpoints | Backend | 🔴 High |
| **GAP-02** | Multi-Tenancy | Scope Customer Debt History Query by Vendor ID | Backend | 🔴 High |
| **GAP-03** | Payments | Dynamic Frontend Redirect URLs for PayMongo Checkout | Backend | 🟡 Medium |
| **GAP-04** | POS / Inventory | Order Cancellation & Voiding (Stock & Debt Reversal) | Backend & Web/Mobile | 🟡 Medium |
| **GAP-05** | Authentication | Password Recovery / Forgot Password Email Flow | Backend & Web/Mobile | 🟡 Medium |
| **GAP-06** | API Design | Standardized HTTP Error Responses on User Registration | Backend & Web | 🟢 Low |
| **GAP-07** | Feature Parity | Mobile Notification Center UI & Synchronization | Mobile (Android) | 🟡 Medium |
| **GAP-08** | Feature Parity | Mobile PDF Receipt & Ledger Statement Export | Mobile (Android) | 🟢 Low |

---

## 🔍 Detailed Gap Specifications & Implementation Blueprints

---

### GAP-01: RBAC Role Enforcement on Platform Admin Endpoints
* **Severity:** 🔴 High (Security Vulnerability)
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/core/security/JwtFilter.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/core/security/JwtFilter.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/admin/controller/AdminController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/admin/controller/AdminController.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/core/security/SecurityConfig.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/core/security/SecurityConfig.java)
* **Problem Statement:**
  * Endpoints `/api/admin/stats` and `/api/admin/vendors/analytics` expose platform-wide sales volume, revenue, and store analytics.
  * In `JwtFilter.java`, `UsernamePasswordAuthenticationToken` is instantiated with `new ArrayList<>()` for authorities. Consequently, no roles (`ROLE_ADMIN`, `ROLE_VENDOR`) exist in Spring Security's `SecurityContext`.
  * `AdminController.java` lacks `@PreAuthorize("hasRole('ADMIN')")` or role verification. Any authenticated user holding a vendor JWT can query all other vendors' financial data.
* **Implementation Blueprint:**
  1. In `JwtFilter.java`, fetch the user's role from the database or embed `role` as a claim in `JwtUtils.generateToken(...)`.
  2. Map the role to `SimpleGrantedAuthority("ROLE_" + role.toUpperCase())` and pass it to `UsernamePasswordAuthenticationToken`.
  3. Enable `@EnableMethodSecurity` in `SecurityConfig.java`.
  4. Annotate `AdminController` methods with `@PreAuthorize("hasRole('ADMIN')")`.
  5. Add unit test verifying that a `ROLE_VENDOR` request to `/api/admin/stats` returns `403 Forbidden`.

---

### GAP-02: Scope Customer Debt History Query by Vendor ID
* **Severity:** 🔴 High (Multi-Tenant Data Leak)
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderHistoryController.java#L24-L28`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderHistoryController.java#L24-L28)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/repository/OrderRepository.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/repository/OrderRepository.java)
* **Problem Statement:**
  * When `customerId` is passed to `/api/orders/history?vendorId=...&customerId=...`, the controller runs:
    ```java
    orders = orderRepository.findAll().stream()
            .filter(o -> customerId.equals(o.getCustomerId()))
            .collect(Collectors.toList());
    ```
  * This executes a full table scan and discards the `vendorId` filter. A vendor querying debt history for a customer ID could theoretically see orders from another store if customer IDs overlap or are forged.
* **Implementation Blueprint:**
  1. Add repository method `List<Order> findByCustomerIdAndVendorId(Long customerId, Long vendorId)`.
  2. Update `OrderHistoryController` to query database directly with both parameters.
  3. Add a test asserting cross-tenant orders are filtered out.

---

### GAP-03: Dynamic Frontend Redirect URLs for PayMongo Checkout
* **Severity:** 🟡 Medium (Operational / Cloud Deployment Issue)
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/payment/controller/PaymentController.java#L31-L32`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/payment/controller/PaymentController.java#L31-L32)
  * [`backend/src/main/resources/application.properties`](file:///backend/src/main/resources/application.properties)
* **Problem Statement:**
  * `successUrl` and `cancelUrl` are hardcoded strings:
    ```java
    String successUrl = "http://localhost:5173/payment-success";
    String cancelUrl = "http://localhost:5173/payment-cancel";
    ```
  * In production (e.g., Vercel deployment) or when interacting via physical Android devices over local Wi-Fi or mobile data, PayMongo redirects the client to `localhost:5173`, causing a broken page error.
* **Implementation Blueprint:**
  1. Introduce property `app.frontend.url=${FRONTEND_URL:http://localhost:5173}` in `application.properties`.
  2. Inject via `@Value("${app.frontend.url}") private String frontendUrl;` in `PaymentController`.
  3. Optionally allow the client to pass custom return paths or dynamic callback origins in the request payload.

---

### GAP-04: Order Cancellation & Voiding Mechanism (POS Mistake Handling)
* **Severity:** 🟡 Medium (Core Business Workflow)
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/service/OrderService.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/service/OrderService.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/order/controller/OrderController.java)
  * [`web/src/features/transactions/Transactions.jsx`](file:///web/src/features/transactions/Transactions.jsx)
* **Problem Statement:**
  * Orders have status `CANCELLED` defined in the domain, but there is no API endpoint or UI action to void an order.
  * When a sale is completed, stock is decremented immediately and credit sales increment customer `currentDebt`.
  * If a cashier enters an incorrect transaction or a customer cancels an item, the store owner cannot reverse it without manual DB intervention.
* **Implementation Blueprint:**
  1. Add `@Transactional public Order cancelOrder(Long orderId, Long vendorId, String reason)` in `OrderService`.
  2. Verify order belongs to `vendorId` and is not already `CANCELLED`.
  3. Restock inventory by iterating `order.getItems()` and adding quantities back to `Product`.
  4. If order status was `DEBT`, decrement customer `currentDebt` by `order.getTotalAmount()`.
  5. Set order status to `CANCELLED` and create an audit notification.
  6. Expose `POST /api/orders/{id}/cancel` in `OrderController`.
  7. Add "Void Order" button with confirmation modal in `Transactions.jsx` (Web).

---

### GAP-05: Password Recovery / Forgot Password Email Flow
* **Severity:** 🟡 Medium (User Management)
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/notification/service/EmailService.java`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/notification/service/EmailService.java)
  * [`web/src/features/auth/Login.jsx`](file:///web/src/features/auth/Login.jsx)
* **Problem Statement:**
  * Authentication currently only provides register and login.
  * If a vendor forgets their password, they cannot regain access without direct database edits.
* **Implementation Blueprint:**
  1. Add `resetToken` and `resetTokenExpiry` fields to `User` entity (or create `PasswordResetToken` table).
  2. Implement `POST /api/auth/forgot-password`: generates a secure random token with 15-minute expiry and sends a branded reset link via `EmailService`.
  3. Implement `POST /api/auth/reset-password`: validates token, hashes new password with `BCryptPasswordEncoder`, and clears token.
  4. Add "Forgot Password?" trigger on Web `Login.jsx` and Mobile `LoginActivity.kt`.

---

### GAP-06: Standardized HTTP Error Responses on User Registration
* **Severity:** 🟢 Low (API Quality & Clean Architecture)
* **Affected Files:**
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java#L38-L49`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java#L38-L49)
  * [`web/src/features/auth/Register.jsx`](file:///web/src/features/auth/Register.jsx)
* **Problem Statement:**
  * `AuthController.register(...)` returns a plain string: `"Error: Email already exists!"` with HTTP `200 OK`.
  * Standard REST convention requires HTTP `400 Bad Request` or `409 Conflict` with a structured JSON body `{"error": "Email already exists"}`.
* **Implementation Blueprint:**
  1. Update `register` return type to `ResponseEntity<?>`.
  2. Return `ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already exists"))`.
  3. Update Web `Register.jsx` error handler to catch Axios error response body rather than parsing plain 200 text.

---

### GAP-07: Mobile In-App Notification Center
* **Severity:** 🟡 Medium (Feature Parity)
* **Affected Files:**
  * [`mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/`](file:///mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/)
  * [`backend/src/main/java/edu/cit/tapales/saritrack/feature/notification/`](file:///backend/src/main/java/edu/cit/tapales/saritrack/feature/notification/)
* **Problem Statement:**
  * The React web client has a live notification bell dropdown displaying low-stock alerts, utang additions, and digital payment confirmations.
  * The Android mobile app has no notification interface, leaving mobile-only cashiers uninformed about backend alerts.
* **Implementation Blueprint:**
  1. Add `NotificationApiService` in mobile `core/api/` mapping `/api/notifications?vendorId=...`.
  2. Create `NotificationBottomSheet.kt` or `NotificationActivity.kt` with a RecyclerView.
  3. Add notification bell icon in `MainActivity` toolbar with an unread badge counter.

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

Before committing any fix for the items in this backlog:
1. **Never commit directly to `main`:** Use `feat/<slice>-...` or `fix/<slice>-...`.
2. **Execute all regression suites:**
   * Backend: `cd backend && .\mvnw.cmd test` (143/143 must pass)
   * Web: `cd web && npx vitest run` (102/102 must pass)
   * Mobile: `cd mobile && .\gradlew.bat testDebugUnitTest` (112/112 must pass)
3. Maintain multi-tenancy invariants: every query affecting products, sales, customers, or stock **must filter by `vendor_id`**.
