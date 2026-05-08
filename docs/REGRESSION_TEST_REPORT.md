# Full Regression Test Report - SariTrack

## 1. Project Information
*   **Project Name:** SariTrack
*   **Developer:** Christian Tapales
*   **Course:** IT342 - System Integration and Architecture
*   **Date:** May 8, 2026
*   **Status:** ✅ ALL TESTS PASSED

## 2. Refactoring Summary
The SariTrack project was migrated from a standard multi-tier layout to a **Vertical Slice Architecture (VSA)**. 
*   **Backend:** Grouped by features (Auth, Product, Order, Notification, Payment).
*   **Frontend (Web/Mobile):** Modularized components to reflect the same feature slices.
*   **Result:** Improved maintainability and isolation of business logic.

## 3. Updated Project Structure
```text
SariTrack/
├── backend/src/main/java/edu/cit/tapales/saritrack/
│   ├── core/           # Shared cross-cutting concerns (Security, Currency)
│   └── feature/        # Vertical Slices (Auth, Product, Order, Notification, Payment)
├── web/src/
│   ├── components/     # Reusable UI Slices
│   └── pages/          # Feature-based Page Slices
└── mobile/app/src/main/java/edu/cit/tapales/saritrack/
    └── feature/        # Mobile Feature Packages
```

## 4. Test Plan & Automated Evidence
The system was validated using a hybrid approach of automated backend unit tests and manual cross-platform UI verification.

### 4.1 Automated Test Suite Results
| Module | Test Coverage | Result |
| :--- | :--- | :--- |
| **Order/POS** | Stock deduction and sales finalization | **PASSED** |
| **Admin** | Vendor analytics and status tiering | **PASSED** |
| **Notifications** | Low-stock alerts and spam prevention | **PASSED** |
| **Currency** | Multi-currency data mapping (PHP/USD/EUR) | **PASSED** |
| **Payment** | PayMongo Security Validation | **PASSED** |
| **Product** | Barcode Lookup & API Fallback | **PASSED** |

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
| **TC-001** | Standard JWT Login | User can login and receive JWT token successfully. | **PASSED** |
| **TC-002** | Google OAuth Sync | User can sync account with Google and auto-register. | **PASSED** |
| **TC-003** | Account Updates | Profile name updates persist in database and UI. | **PASSED** |
| **TC-004** | Secure Logout | JWT is cleared and user is redirected to Login. | **PASSED** |
| **TC-005** | Product & Image | Supabase Image Uploads and Product CRUD functional. | **PASSED** |
| **TC-006** | Barcode Lookup | External API auto-populates product details. | **PASSED** |
| **TC-007** | Camera Scanner | Mobile hardware scanner detects barcodes correctly. | **PASSED** |
| **TC-008** | Search & Filter | Real-time filtering of product list verified. | **PASSED** |
| **TC-009** | Product Deletion | Data is removed correctly from DB and frontend. | **PASSED** |
| **TC-010** | Low-Stock Alerts | Notifications trigger when inventory hits threshold. | **PASSED** |
| **TC-011** | POS Cart (Cash) | Sale recorded and stock deducted for cash transactions. | **PASSED** |
| **TC-012** | Credit (Utang) | Debt recorded correctly for selected customers. | **PASSED** |
| **TC-013** | PayMongo Settle | Digital settlement via PayMongo Sandbox successful. | **PASSED** |
| **TC-014** | Transaction Logs | History view shows accurate sales data and totals. | **PASSED** |
| **TC-015** | CRM Management | New customers can be added and managed in list. | **PASSED** |
| **TC-016** | System Monitoring | Admin Panel shows global vendor health and stats. | **PASSED** |
| **TC-017** | Analytics Dash | Charts render live sales and vendor performance. | **PASSED** |
| **TC-018** | Data Portability | CSV/PDF exports download with correct data formats. | **PASSED** |
| **TC-019** | Currency Exchange| Live rates apply correctly to all product prices. | **PASSED** |
| **TC-020** | Theme Engine | Seamless Dark/Light mode transition verified. | **PASSED** |

---

## 5.1 Manual Verification Evidence

#### A. Authentication & Dashboard (Web)
*Evidence for: TC-001, TC-002, TC-003, TC-004, TC-017, TC-020*

![LogIn](./assets/LogIn.png)
![GoogleOAuth](./assets/GoogleOAuth.png)
![AdminDashboard](./assets/AdminDashboard.png)
![MainDashBoard(Light)](./assets/MainDashBoard(Light).png)
![MainDashBoard(Dark)](./assets/MainDashBoard(Dark).png)

#### B. Inventory & Mobile Scanning (Mobile)
*Evidence for: TC-005, TC-006, TC-007, TC-008, TC-010*

![MobileDashboard](./assets/MobileDashboard.jpg)
![MobileInventory](./assets/MobileInventory.jpg) 
![MobileBarcodeScanner](./assets/MobileBarcodeScanner.jpg)

#### C. POS System & Financials (Web/Mobile)
*Evidence for: TC-011, TC-012, TC-014, TC-019*

![TransactionLogs](./assets/TransactionLogs.png) 
![InventoryDeductionPart1](./assets/InventoryDeductionPart1.png) 
![InventoryDeductionPart2](./assets/InventoryDeductionPart2.png) 
![InventoryDeductionPart3](./assets/InventoryDeductionPart3.png) 
![ListahanUtang](./assets/ListahanUtang.png)

#### D. Admin & Customer Management (Web)
*Evidence for: TC-015, TC-016, TC-018*

![AdminDashboard](./assets/AdminDashboard.png)
![AddingCustomerPart02](./assets/AddingCustomerPart02.png) 
![AddingCustomerPart01](./assets/AddingCustomerPart01.png)

---

## 6. Issues Found & Fixes Applied
During the refactoring and regression testing phase, the following issues were identified and resolved.

| Issue | Feature | Description | Fix Applied |
| :--- | :--- | :--- | :--- |
| **Android Manifest Error** | Mobile | Activity paths were broken after moving files. | Updated `AndroidManifest.xml` with fully qualified names. |
| **Import Conflicts** | Backend | Unused/redundant imports caused IDE warnings. | Performed a code cleanup pass to remove dead imports. |
| **Notification Duplication**| Backend | Low stock alerts triggered multiple times. | Implemented "Spam Prevention" logic in `NotificationService`. |
| **Duplicate Variable** | Backend | IDE reported naming conflict in product lookup. | Refactored tests to use class-level constants. |

---

## 7. Conclusion
The transition to a **Vertical Slice Architecture** was completed successfully. The 100% pass rate in the automated suite and the successful manual verification confirm that SariTrack is stable, modular, and ready for final submission.

---
*End of Report.*
