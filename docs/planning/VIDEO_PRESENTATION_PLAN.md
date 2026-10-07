# 🎥 SariTrack Video Presentation Plan

This plan is structured for a **7-to-8-minute presentation** (fitting perfectly within the 5–10 minute requirement). It ensures you address every criterion on the grading rubric to secure maximum points.

---

## 📋 Rubric Alignment Strategy

| Rubric Criteria | Weight | How This Plan Achieves "Excellent" |
| :--- | :--- | :--- |
| **System Introduction & Overview** | 10% | Clear introduction of Christian Kyle Tapales, SariTrack's mission, problem statement (manual lists vs. digital POS), and target audience. |
| **Presentation of Main Features** | 30% | Step-by-step description of multi-tenancy, hybrid POS checkout, offline capabilities, debt management, and API integrations. |
| **Architecture & Interaction** | 30% | Deep dive into the **Three-Tier Architecture** and your refactored **Vertical Slice Architecture (VSA)** using detailed Mermaid data-flow diagrams. |
| **Proof of Implementation** | 20% | Walkthrough of the folder structure, backend controller slices, and executing your suite of **357 automated tests** (Backend, Web, Mobile). |
| **Demo Quality & Voice-over** | 10% | Minute-by-minute script syncing UI actions with architectural explanations and system integration terms. |

---

## ⏱️ Video Timeline at a Glance

```mermaid
gantt
    title Video Presentation Timeline (Total: 8 Minutes)
    dateFormat  m:s
    axisFormat %M:%S
    
    Self-Introduction & Intro     :active, intro, 00:00, 01:00
    System Features & Value Prop  :feat, 01:00, 02:15
    Architecture & Integration    :arch, 02:15, 03:45
    Code & Test Proof             :proof, 03:45, 04:45
    Live System Demonstration      :demo, 04:45, 07:30
    Summary & Conclusion          :conclusion, 07:30, 08:00
```

---

## 🗂️ Slide-by-Slide Presentation Script

### Slide 1: Title & Self-Introduction
*   **Visuals:** Title card with SariTrack Logo (`#16A394` teal theme), Student Name, Course & Section, and University branding.
*   **Duration:** 30 seconds
*   **Speaker Script:**
    > *"Good day, everyone! I am Christian Kyle Bayarcal Tapales, a 3rd-year BS Information Technology student under the course IT342: System Integration and Architecture. Today, I am proud to present my final individual system project: **SariTrack**—a premium, multi-tenant SaaS POS and credit-tracking ecosystem designed to digitalize traditional sari-sari stores."*

---

### Slide 2: Problem Statement & Project Goal
*   **Visuals:** Side-by-side comparison:
    *   *Left:* Photo of a paper ledger ("listahan") with red "X" (cluttered, lost books, unrecorded debts, zero stock tracking).
    *   *Right:* SariTrack clean interfaces with checkmarks (automated stock levels, chronological credit timelines, multi-payment support).
*   **Duration:** 45 seconds
*   **Speaker Script:**
    > *"Traditional sari-sari stores serve as the economic backbone of communities. However, they rely heavily on manual paper ledgers—locally known as 'listahan'—to record customer credits ('utang'). This leads to untracked debts, inventory leakages, and stockouts. 
    > 
    > **SariTrack** bridges this gap. It replaces manual ledgers with a robust digital POS and CRM ecosystem, giving store owners professional tools to manage inventory, secure records, track customer debts, and accept digital payments."*

---

### Slide 3: System Architecture & Data Flow
*   **Visuals:** Three-tier architecture diagram showing the connection between:
    1.  **Client Tier:** React Web Portal & Android Kotlin POS.
    2.  **Server Tier:** Spring Boot API with JWT Security & Vertical Slices.
    3.  **Data/External Tier:** Supabase PostgreSQL, Supabase Cloud Storage, and 3rd-party APIs (PayMongo, SMTP, ExchangeRate).
*   **Duration:** 1 minute 15 seconds
*   **SIA Concept Focus:** Client-Server Communication, REST APIs, Modular Separation of Concerns.
*   **Speaker Script:**
    > *"Architecturally, SariTrack is built on a **Three-Tier Architecture**. 
    > 
    > For the **Client Tier**, we have a premium React Web dashboard for administrators and shop owners to review data, and a native Android Kotlin mobile app optimized for barcode scanning on the floor. 
    > 
    > These clients communicate via secure HTTPS JSON requests with our **Server Tier**—a Java Spring Boot 3 API. The backend intercepts requests using a JWT security filter chain, ensuring strict multi-tenant boundaries. 
    > 
    > Finally, the **Data Tier** utilizes PostgreSQL hosted on Supabase and Supabase public buckets for image CDNs. Our integration layer connects to Google OAuth for login, PayMongo for sandbox payments, ExchangeRate API for daily currency updates, and Gmail SMTP for automatic alerts."*

---

### Slide 4: Architectural Spotlight: Vertical Slice Refactoring
*   **Visuals:** Code folders comparison showing the transition to Vertical Slice Architecture (VSA).
    *   *Traditional:* `controller/`, `service/`, `repository/` package clutter.
    *   *Vertical Slice:* Feature-based folders (`feature/auth/`, `feature/product/`, `feature/payment/`).
*   **Duration:** 45 seconds
*   **SIA Concept Focus:** Maintainability, Cohesion vs. Coupling, Component Isolation.
*   **Speaker Script:**
    > *"To adhere to modern systems integration standards, I refactored the codebase to follow **Vertical Slice Architecture**. 
    > 
    > Instead of organizing code by technical layers, files are packaged by features—such as Auth, Product, Order, Notification, and Payment. This minimizes coupling and ensures that a change in the product logic does not impact the billing checkout service. Each slice contains its controller, service logic, and database handler, which dramatically improves development agility and maintainability."*

---

### Slide 5: Proof of Implementation: Automated Test Suite
*   **Visuals:** Screenshots of terminal test reports and summary panel:
    *   Backend Tests: `[143/143 PASSED]` (Spring Boot / JUnit 5)
    *   Web Tests: `[102/102 PASSED]` (React / Vitest)
    *   Mobile Tests: `[112/112 PASSED]` (Android Kotlin / JUnit)
    *   Total Coverage: **357 / 357 passing tests**.
*   **Duration:** 45 seconds
*   **SIA Concept Focus:** Integration Testing, Regression Prevention.
*   **Speaker Script:**
    > *"To verify that our refactored vertical slices operate flawlessly, I implemented a robust, multi-layered automated test suite. 
    > 
    > Across our monorepo, we have **357 automated tests** running. The Spring Boot backend runs 143 integration tests covering JWT filters, database transactions, and service logic. The React web app runs 102 Vitest UI tests. The Android app executes 112 JUnit helpers and session managers. 100% of these tests pass, guaranteeing zero regression across all integrated subsystems."*

---

## 🖥️ Live System Demonstration Script (3 Minutes)

*This is the core walkthrough. You will transition from your slides to sharing your screen showing the web portal, Android device/emulator, and the backend running.*

```text
💡 Pro-Tip for the Demo: Split your screen or have both the React Web Portal and the Android Mobile App visible. Keep your IDE or terminal visible in a background corner.
```

### Part A: Authentication & Dynamic Dashboard (Web & Mobile)
*   **Time:** 04:45 - 05:30 (45s)
*   **Action on Screen:** 
    1. Show the Web login page. Log in using Google OAuth2.
    2. Show the Android App login screen, showing the password-visibility toggle and Google Account picker. Log in on mobile.
    3. Show the dashboards side-by-side. Point out the synced sales statistics.
*   **Speaker Script:**
    > *"Let's begin the system demo. On the web portal, we log in using Google OAuth2, which synchronizes our session securely. On our Android application, we log in as well. 
    > 
    > Once logged in, both clients display a dynamic dashboard. Because we use a multi-tenant layout, store owners only see their sales. The dashboard displays key metrics like Today's Sales, Low Stock alert lists, and Outstanding Debt in real time."*

### Part B: Inventory, Barcode Scanning, & Cloud Storage (Mobile to Cloud)
*   **Time:** 05:30 - 06:15 (45s)
*   **Action on Screen:** 
    1. On the Android App, open the Product List. Show that images load even offline (Glide cached).
    2. Tap 'Scan Barcode' on mobile. Trigger a test scan (e.g., using a physical barcode or camera simulator).
    3. Point out that the product metadata is retrieved automatically from the Open Barcode API.
    4. Create a new product, capture/select an image, and save. Point to the Supabase storage console to show the newly uploaded image.
*   **Speaker Script:**
    > *"Next, let's explore Inventory. On mobile, product images are cached locally using Glide for offline reliability. When we tap 'Scan Barcode', Google ML Kit opens the camera to scan UPC codes, fetching product details from the Open Barcode API. 
    > 
    > When a vendor adds a new product, the image is uploaded directly to a Supabase Cloud Storage bucket, while the metadata is persisted in the PostgreSQL database. This completes our cloud storage and database integration."*

### Part C: Hybrid POS Checkout & Sandbox Integration (Web/Mobile to PayMongo)
*   **Time:** 06:15 - 07:00 (45s)
*   **Action on Screen:** 
    1. On mobile, add items to the cart. 
    2. Choose **Digital Payment** (PayMongo) checkout.
    3. The app redirects to the PayMongo Secure Sandbox page. Show a simulated GCash payment.
    4. Once paid, show the automatic redirect back to the app success screen, showing the transaction registered.
    5. Show the transaction list showing the updated stock count and sales graph.
*   **Speaker Script:**
    > *"Now, let's process a transaction. In the Mobile POS, I will add items to the cart. I can checkout using Cash, Debt, or Digital Payments. Let's select Digital Payment. 
    > 
    > The system integrates with PayMongo Sandbox. We are securely redirected to their checkout gateway. After completing the sandbox transaction, PayMongo fires a secure webhook back to our Spring Boot backend. 
    > 
    > The backend validates the cryptographic signature of the webhook, updates the order status to PAID, decrements the product inventory, and redirects the mobile interface to a professional receipt success screen."*

### Part D: Debt Tracking (Listahan) & PDF/CSV Reporting
*   **Time:** 07:00 - 07:30 (30s)
*   **Action on Screen:** 
    1. Go to the "Listahan" page on the Web Portal.
    2. Select a customer, click "Settle Debt". Record a payment.
    3. Download the PDF report or CSV spreadsheet of transactions.
*   **Speaker Script:**
    > *"Finally, we check the CRM 'Listahan' tab. Here we see customers with active debts. We can settle debts chronologically. 
    > 
    > To support business audits, store owners can export these logs. With a single click, the system generates a professional PDF receipt and compiles detailed CSV spreadsheets of historical sales, ready for offline accounting."*

---

### Slide 6: Summary & Technical Highlights (Conclusion)
*   **Visuals:** Summary card: 
    *   3-Tier Architecture & Vertical Slice
    *   5 External System Integrations (PayMongo, Google OAuth, ExchangeRate, SMTP, Open Barcode)
    *   357 Automated Tests / 100% Pass Rate
    *   Production-Ready Status
*   **Duration:** 30 seconds
*   **Speaker Script:**
    > *"In conclusion, SariTrack successfully digitalizes the traditional storefront by integrating cloud storage, third-party payment channels, live currency data, and automated scanner frameworks. 
    > 
    > By restructuring the system using Vertical Slice Architecture and securing it with full test coverage, SariTrack offers a robust, production-ready POS experience for small-scale merchants. 
    > 
    > Thank you very much for listening, and I am open to your questions."*

---

## 💻 Code Snippets & Files to Show in IDE (Proof of Implementation)

Be ready to open these files in Android Studio/VS Code to prove you wrote the code:

1.  **Backend Slices:**
    *   [AuthController.java](file:///c:/Users/USER/Documents/School/3rd%20Year%202nd%20Semester/IT342%20-%20System%20Integration%20and%20Architecture/Final%20Project/IT342-Tapales-SariTrack/backend/src/main/java/edu/cit/tapales/saritrack/feature/auth/controller/AuthController.java) (OAuth2 & JWT endpoints)
    *   [PayMongoService.java](file:///c:/Users/USER/Documents/School/3rd%20Year%202nd%20Semester/IT342%20-%20System%20Integration%20and%20Architecture/Final%20Project/IT342-Tapales-SariTrack/backend/src/main/java/edu/cit/tapales/saritrack/feature/payment/service/PayMongoService.java) (Integration logic & API calls)
    *   [WebhookController.java](file:///c:/Users/USER/Documents/School/3rd%20Year%202nd%20Semester/IT342%20-%20System%20Integration%20and%20Architecture/Final%20Project/IT342-Tapales-SariTrack/backend/src/main/java/edu/cit/tapales/saritrack/feature/payment/controller/WebhookController.java) (Signature validation & callback listener)
2.  **Mobile Slices:**
    *   [RetrofitClient.kt](file:///c:/Users/USER/Documents/School/3rd%20Year%202nd%20Semester/IT342%20-%20System%20Integration%20and%20Architecture/Final%20Project/IT342-Tapales-SariTrack/mobile/app/src/main/java/edu/cit/tapales/saritrack/core/api/RetrofitClient.kt) (API Client configurations)
    *   [BarcodeScannerFragment.kt](file:///c:/Users/USER/Documents/School/3rd%20Year%202nd%20Semester/IT342%20-%20System%20Integration%20and%20Architecture/Final%20Project/IT342-Tapales-SariTrack/mobile/app/src/main/java/edu/cit/tapales/saritrack/feature/pos/BarcodeScannerFragment.kt) (ML Kit camera integration)
3.  **Web Hooks/Slices:**
    *   [PointOfSale.jsx](file:///c:/Users/USER/Documents/School/3rd%20Year%202nd%20Semester/IT342%20-%20System%20Integration%20and%20Architecture/Final%20Project/IT342-Tapales-SariTrack/web/src/pages/PointOfSale.jsx) (VSA feature slice for POS cart and PayMongo payment invocation)
