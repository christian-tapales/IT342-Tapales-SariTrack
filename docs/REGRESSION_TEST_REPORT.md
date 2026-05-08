# SariTrack - Full Regression Test Report

## 1. Project Information
*   **Student Name:** Christian Kyle Tapales
*   **Project Name:** SariTrack (SaaS POS for Sari-Sari Stores)
*   **Date of Submission:** May 8, 2026
*   **Architecture Migration:** Layered Architecture ➔ Vertical Slice Architecture (VSA)

---

## 2. Refactoring Summary (Vertical Slice Architecture)
The project was successfully refactored from a technical-layered structure to a feature-based structure. This migration ensures that each feature (Auth, Inventory, POS, Admin) is self-contained, improving maintainability and scalability.

### 2.2 Updated Project Structure (Visual)
```text
SariTrack/
├── backend/src/main/java/.../feature/
│   ├── auth/          (Auth Logic)
│   ├── inventory/     (Product CRUD)
│   ├── order/         (POS & Cart)
│   ├── payment/       (PayMongo logic)
│   └── admin/         (Dashboard stats)
├── web/src/feature/
│   ├── inventory/     (Cloud Uploads)
│   ├── pos/           (Real-time Cart)
│   └── reports/       (Analytics)
└── mobile/app/src/main/java/.../feature/
    ├── auth/          (Login/Register)
    ├── inventory/     (Product List)
    └── pos/           (Scanner & Checkout)
```

---

## 3. Test Plan Documentation
The full Software Test Plan is documented in `docs/SOFTWARE_TEST_PLAN.md`, covering 100% of the Functional Requirements (FR) defined in the SDD. It includes a hybrid approach of Manual UI Testing and Automated Backend Logic Testing.

---

## 4. Automated Test Evidence (Backend)
To provide absolute proof of system integrity, 11 automated test cases were executed.

### 4.1 Test Suite Summary
| Test Class | Logic Verified | Status |
| :--- | :--- | :--- |
| `OrderServiceTest` | Sales, Debt, & Digital Logic | **PASSED** |
| `AdminServiceTest` | Analytics & Tiers | **PASSED** |
| `NotificationServiceTest` | Alerts & Spam Prevention | **PASSED** |
| `CurrencyServiceTest` | Exchange Rates | **PASSED** |
| `PayMongoServiceTest` | Security Validation | **PASSED** |
| `ProductLookupServiceTest` | Barcode Lookup Logic | **PASSED** |

### 4.2 Automation Evidence
![Test Result 1](./assets/AutomatedTest-001.png)
![Test Result 2](./assets/AutomatedTest-002.png)
![Test Result 3](./assets/AutomatedTest-003.png)
![Test Result 4](./assets/AutomatedTest-004.png)
> *Evidence showing: `BUILD SUCCESS` and `Tests run: 13`*

---

## 5. Regression Test Results (Manual)
The following Functional Requirements were manually verified across platforms.

| Test ID | Requirement | Observation | Result |
| :--- | :--- | :--- | :--- |
| TC-001 | Authentication | JWT Token issuance and persistence verified. | **PASSED** |
| TC-003 | Inventory CRUD | Supabase Image Uploads functional. | **PASSED** |
| TC-005 | POS System | Real-time stock deduction verified. | **PASSED** |
| TC-007 | Payments | PayMongo Checkout link generation verified. | **PASSED** |

---

## 6. Issues Found & Fixes Applied
During the refactoring and regression testing phase, the following issues were identified and resolved.

| Issue | Feature | Description | Fix Applied |
| :--- | :--- | :--- | :--- |
| **Android Manifest Error** | Mobile | Activity paths were broken after moving files to feature packages. | Updated `AndroidManifest.xml` with fully qualified package names. |
| **Import Conflicts** | Backend | Unused/redundant imports caused IDE warnings. | Performed a code cleanup pass to remove dead imports. |
| **Notification Duplication** | Backend | Low stock alerts triggered multiple times for same product. | Implemented "Spam Prevention" logic in `NotificationService`. |

---

## 7. Conclusion
The transition to a **Vertical Slice Architecture** was completed without data loss or functional regression. The 100% pass rate in the automated suite (11/11 tests) and successful manual UI verification confirm that the system is stable and ready for final deployment.

---
*End of Report.*
