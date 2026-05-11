# Software Test Plan (STP) - SariTrack VSA Refactor

## 1. Introduction
This document outlines the comprehensive test plan for the SariTrack project following its migration to Vertical Slice Architecture (VSA). The goal is to ensure that all functional requirements are intact and that the refactoring has not introduced regressions.

## 2. Testing Strategy
*   **Unit Testing:** Backend business logic validation using JUnit 5 and Mockito.
*   **Regression Testing:** Full system walkthrough to verify end-to-end flows.
*   **Integration Testing:** Verify external APIs (Supabase Storage, PayMongo, ExchangeRates).

---

## 3. Functional Requirements Coverage
| Module | Requirement | Status |
| :--- | :--- | :--- |
| **Auth** | JWT Security, OAuth Sync, Session Management | Covered |
| **Inventory** | Product CRUD, Supabase Images, Barcode Lookup, Alerts | Covered |
| **POS** | Cart Logic, Stock Deduction, Credit/Debt Tracking | Covered |
| **Payments** | PayMongo Integration, Webhook Processing | Covered |
| **Reports** | Analytics Dashboard, CSV/PDF Export, Admin Monitoring | Covered |

---

## 4. Test Case Matrix (Functional Requirements)

| Test ID | Module | Category | Description / Objective | Steps to Execute | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-001** | Auth | Authentication | Verify Standard JWT Login | 1. Navigate to `/login` <br> 2. Enter email & password <br> 3. Click Login | Redirects to Dashboard, JWT stored | Pending |
| **TC-002** | Auth | Authentication | Verify Google OAuth Sync | 1. Click "Google Account" <br> 2. Authenticate via Google | Auto-creates user if new, issues JWT | Pending |
| **TC-003** | Profile | Account | Verify Account Updates | 1. Update Profile Name in Settings | Changes persist after refresh | Pending |
| **TC-004** | Auth | Session | Verify Secure Logout | 1. Click Logout | JWT is cleared, redirected to Login | Pending |
| **TC-005** | Inventory | Storage | Verify Product Creation & Image | 1. Add product with Supabase Image | Image URL saved, Product appears in list | Pending |
| **TC-006** | Inventory | API Lookup | Verify External Barcode Lookup | 1. Enter known barcode in form | Fields auto-populate via external API | Pending |
| **TC-007** | Mobile | Hardware | Verify Barcode Camera Scanner | 1. Open Scanner on Android <br> 2. Point at barcode | Hardware detects code, sends data to API | Pending |
| **TC-008** | Inventory | Navigation | Verify Search & Filter | 1. Type in Search Bar | List updates in real-time | Pending |
| **TC-009** | Inventory | Data Mgt | Verify Product Deletion | 1. Select Product <br> 2. Click Delete | Product removed from DB and UI | Pending |
| **TC-010** | Inventory | Intelligence | Verify Low-Stock Alerts | 1. Sell product until stock < threshold | Dashboard shows alert notification | Pending |
| **TC-011** | POS | Sales | Verify Cart Checkout (Cash) | 1. Add to cart <br> 2. Pay with Cash | Order = "PAID", Stock deducted | Pending |
| **TC-012** | POS | Credit | Verify Debt Recording (Utang) | 1. Select Customer <br> 2. Pay via Debt | Order = "DEBT", Customer balance increased | Pending |
| **TC-013** | Payment | Financial | Verify Digital Settlement | 1. Click "Settle via PayMongo" <br> 2. Complete payment | Webhook fires, Status = "PAID" | Pending |
| **TC-014** | POS | Reporting | Verify Transaction Logs | 1. Go to Transactions | List of past sales appears correctly | Pending |
| **TC-015** | CRM | Customers | Verify Customer Management | 1. Add New Customer | Customer appears in CRM list | Pending |
| **TC-016** | Admin | Monitoring | Verify Global System Monitoring | 1. Access Admin Panel | Views global stats and platform health | Pending |
| **TC-017** | Reports | Analytics | Verify Analytics Dashboard | 1. Go to Dashboard | UI renders accurate Sales/Orders charts | Pending |
| **TC-018** | Reports | Data | Verify Data Export (CSV/PDF) | 1. Click Export CSV/PDF | Browser downloads formatted file | Pending |
| **TC-019** | Core | Localization| Verify Currency Exchange | 1. Select USD/EUR in settings | Prices update based on live rates | Pending |
| **TC-020** | UI/UX | Theme | Verify Dark/Light Mode Toggle | 1. Click Theme Toggle | UI switches between Light/Dark themes | Pending |

---

## 5. Automated Test Implementation (Backend)
To provide robust regression evidence, automated tests have been implemented for critical path logic using JUnit and Mockito.

### 5.1 Test Suite Summary
| Test Class | Method | Targeted Logic | Status |
| :--- | :--- | :--- | :--- |
| `OrderServiceTest` | `testCompleteSale_DeductsStock` | Inventory stock deduction logic | **PASSED** |
| `OrderServiceTest` | `testDigitalOrderFinalization` | PayMongo settlement logic | **PASSED** |
| `AdminServiceTest` | `testVendorAnalyticsStatus` | Vendor performance tier logic | **PASSED** |
| `NotificationServiceTest` | `testSyncLowStock` | Automated low stock alert logic | **PASSED** |
| `NotificationServiceTest` | `testSpamPrevention` | Duplicate notification avoidance | **PASSED** |
| `CurrencyServiceTest` | `testPHPBaseRate` | Multi-currency base logic (PHP=1.0) | **PASSED** |
| `CurrencyServiceTest` | `testMajorCurrencies` | Currency data mapping verification | **PASSED** |
| `PayMongoServiceTest` | `testSignatureValidation` | Payment webhook cryptographic security | **PASSED** |
| `ProductLookupServiceTest` | `testLookupSuccess` | External barcode API success path | **PASSED** |
| `ProductLookupServiceTest` | `testLookupFallback` | Barcode API failure fallback | **PASSED** |
| `SaritrackApplicationTests` | `contextLoads` | Spring Boot context initialization | **PASSED** |

## Screenshots of Automated Test Results

![alt text](./assets/AutomatedTest-001.png)
![alt text](./assets/AutomatedTest-002.png)
![alt text](./assets/AutomatedTest-003.png)
![alt text](./assets/AutomatedTest-004.png)

### 5.2 Automation Evidence
* **Tool:** Maven Surefire Plugin
* **Execution Date:** May 8, 2026
* **Result:** `Tests run: 13, Failures: 0, Errors: 0, Skipped: 0`

---
*Generated as part of Phase 3 Refactoring Requirements.*
