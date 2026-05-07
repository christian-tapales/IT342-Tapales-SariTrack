# Vertical Slice Refactoring & Full Regression Testing Plan

This document outlines the step-by-step strategy to complete the final assignment successfully, focusing on transitioning the architecture from a technical layered approach to a feature-based Vertical Slice Architecture (VSA).

## Phase 1: Branching & Preparation (COMPLETED)
1. **Ensure Clean State:** Verified the `main` branch is fully updated and has no pending changes.
2. **Create Branch:** Created a new branch specifically for this assignment: `refactor/vertical-slice`.

## Phase 2: Vertical Slice Refactoring
We will transition the architecture from "Layer-by-Layer" (Controllers, Services, Repositories grouped together) to "Feature-by-Feature" (Auth, Products, Orders grouped together). This improves modularity and maintainability.

### 2.1 Backend (Spring Boot)
*   **Current Structure:** `controller/`, `service/`, `repository/`, `entity/`
*   **Target Structure:** 
    *   `feature/auth/` (AuthController, AuthService, AuthModels)
    *   `feature/product/` (ProductController, ProductService, ProductRepository, Product)
    *   `feature/order/` (OrderController, OrderService, OrderRepository, Order)
    *   `feature/customer/` (CustomerController, CustomerService, CustomerRepository, Customer)
    *   `feature/admin/` (AdminController, AdminService)
    *   `core/` (Shared utilities, config, exceptions, EmailService, PayMongoService)

### 2.2 Web Frontend (React)
*   **Current Structure:** `pages/`, `components/`, `api/`
*   **Target Structure:**
    *   `features/auth/` (Login, Register pages and related components/API calls)
    *   `features/inventory/` (Inventory page, AddProduct modal, Product cards)
    *   `features/transactions/` (Transactions page, export logic)
    *   `features/dashboard/` (Vendor & Admin dashboards, charts)
    *   `core/` (Layouts, API client setup, shared UI like Skeleton)

### 2.3 Mobile App (Android/Kotlin)
*   **Current Structure:** Flat or layered (Activities, Fragments, Models scattered).
*   **Target Structure:**
    *   `feature/auth/` (LoginActivity, RegisterActivity, AuthApiService)
    *   `feature/home/` (HomeFragment, DashboardApiService)
    *   `feature/pos/` (SalesFragment, ScannerBottomSheet, ProductAdapter)
    *   `feature/listahan/` (CreditsFragment, CustomerAdapter)
    *   `core/` (RetrofitClient, SessionManager, Base Classes)

## Phase 3: Test Plan & Automation
1. **Map Functional Requirements:** Extract core requirements from the Revised SDD.
2. **Write Test Cases:** Create a documented matrix of test cases (e.g., "Verify user can add a product", "Verify stock deducts on sale").
3. **Implement Automated Tests:** 
   - Focus on the **Backend** (Spring Boot) using JUnit and Mockito to test core business logic (e.g., OrderService `completeSale` method). This provides solid automated evidence for the report.

## Phase 4: Full Regression Testing
1. **Execute Test Plan:** Manually verify the Web and Mobile apps using the defined test cases.
2. **Run Automated Tests:** Execute the backend test suite and collect evidence (logs/screenshots).
3. **Fix Regressions:** Identify and patch any broken imports, routing issues, or missing dependencies caused by the folder restructure.

## Phase 5: Reporting & Submission
1. **Compile Test Report:** Draft a comprehensive Markdown document containing the Test Plan, Execution Results, and Automation Evidence.
2. **Convert to PDF:** Convert this Markdown file to a PDF for submission (`FullRegressionReport_GroupNo_ProjectName.pdf`).
3. **Commit & Push:** Commit all changes and push the `refactor/vertical-slice` branch to GitHub.
