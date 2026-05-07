# SariTrack 🛒 
> **Your Store, Digitalized. A Premium Multi-Tenant SaaS POS Ecosystem.**

![Status](https://img.shields.io/badge/Status-Production--Ready-16A394?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203-6DB33F?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/Web-React%2019-61DAFB?style=for-the-badge&logo=react)
![Android](https://img.shields.io/badge/Mobile-Android%20Kotlin-3DDC84?style=for-the-badge&logo=android)

SariTrack is a high-performance, multi-tenant platform designed to modernize the traditional sari-sari store ecosystem. By bridging the gap between manual "listahan" systems and modern digital retail, SariTrack empowers store owners with professional management tools across Web and Mobile.

---

## 🏗️ System Architecture

SariTrack is built on a robust **Three-Tier Architecture**:

1.  **`backend/`**: A high-availability Spring Boot 3 API managing RBAC security, atomic transactions, and 3rd-party integrations.
2.  **`web/`**: A premium React management dashboard for vendors to perform bulk inventory management, review analytics, and generate professional reports.
3.  **`mobile/`**: A high-speed Android POS application optimized for real-time barcode scanning and on-the-floor sales.

---

## ✨ Core Features

### 🏪 For Vendors (Store Owners)
*   **Inventory 2.0**: Full CRUD management with **Supabase Image Storage** and international barcode lookup.
*   **Listahan (Debt Management)**: Digital customer credit tracking with settlement history.
*   **Hybrid POS**: Support for Cash, Debt, and **Digital Payments (PayMongo Sandbox)**.
*   **Global Reach**: Real-time currency conversion rates for international product pricing.
*   **Professional Reporting**: Generate and download **CSV/PDF reports** for inventory and sales audits.

### 🛡️ For Administrators
*   **Platform Monitoring**: Global vendor management and activity tracking.
*   **System Integrity**: Strict multi-tenant data isolation and RBAC security.
*   **Email Notifications**: Automated SMTP-based welcome emails and transaction alerts.

---

## 🛠️ Technology Stack (As-Built)

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.x, Spring Security (JWT), JPA/Hibernate |
| **Database** | PostgreSQL (Hosted on Supabase) |
| **Web Portal** | React 19, JavaScript (ES6+), Tailwind CSS 4, Recharts, jsPDF |
| **Mobile POS** | Kotlin, XML Layouts, ML Kit (Barcode Scanning), Retrofit 2, Room |
| **Infrastructure** | Supabase Storage (CDN), PayMongo API, ExchangeRate API |

---

## 🚀 Getting Started

### 1. Running the Backend 🟢
```bash
cd backend
./mvnw spring-boot:run
```
*API available at `http://localhost:8080`*

### 2. Running the Web Client 🌐
```bash
cd web
npm install && npm run dev
```
*Portal available at `http://localhost:5173`*

### 3. Running the Mobile Client 📱
1. Open the `mobile/` directory in **Android Studio (Koala+)**.
2. Connect a physical device or emulator (API 34+).
3. Build and Run (Shift + F10).

---

## 📁 Documentation
Detailed design specs and development roadmaps are available in the `docs/` directory:
*   [Revised SDD (As-Built)](docs/SDD_REVISED_SariTrack.md)
*   [Web Development Roadmap](docs/WEB_DEV_PLAN.md)
*   [Mobile Development Roadmap](docs/MOBILE_DEV_PLAN.md)

---

## 📝 License
Educational project for **IT342 - System Integration and Architecture**.  
Developed by **Christian Kyle B. Tapales**.
