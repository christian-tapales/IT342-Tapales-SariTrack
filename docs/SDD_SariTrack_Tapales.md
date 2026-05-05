# SariTrack System Design Document (SDD)

**Project Title:** SariTrack  
**Author:** Christian Kyle Bayarcal Tapales  
**Version:** 1  
**Date:** February 23, 2026  
**Status:** Final

---

## REVISION HISTORY

| Version | Date | Author | Changes Made | Status |
| :--- | :--- | :--- | :--- | :--- |
| 0.1 | [Date] | [Your Name] | Initial draft | Draft |
| 0.2 | [Date] | [Your Name] | Added API specifications | Review |
| 0.3 | [Date] | [Your Name] | Updated database design | Review |
| 0.4 | [Date] | [Your Name] | Added UI/UX designs | Review |
| 0.5 | [Date] | [Your Name] | Incorporated feedback | Revised |
| 0.6 | [Date] | [Your Name] | Final review and corrections | Final |
| 1 | February 23, 2026 | Christian Kyle Tapales | Baseline version for development | Approved |

---

## 1.0 EXECUTIVE SUMMARY

### 1.1 Project Overview
SariTrack is a multi-tenant SaaS (Software as a Service) platform designed to modernize the traditional sari-sari store ecosystem. The system provides a digital point-of-sale (POS) experience, enabling store owners to move away from manual paper-based logging. It features a **Spring Boot** backend, a **React** web dashboard for management, and an **Android** mobile application for real-time sales scanning.

### 1.2 Objectives
* Develop a multi-tenant management system with secure JWT authentication, Google OAuth, and RBAC (Software Admins vs. Store Vendors).
* Implement a three-tier architecture: Spring Boot (Backend), React (Web Portal), and Android Kotlin/XML (Mobile POS).
* Create RESTful APIs integrating external services: PayMongo Sandbox (debt settlement), Currency Exchange APIs, and SMTP (email notifications).
* Design intuitive interfaces optimized for desktop inventory management and mobile barcode scanning.
* Ensure strict data isolation between vendors and secure file storage for product images.

### 1.3 Scope

#### Included Features:
* User registration and JWT-based authentication.
* Product catalog management with search and barcode scanning.
* Shopping cart management for multi-item transactions.
* Checkout process with digital credit settlement via PayMongo Sandbox.
* Admin panel for comprehensive management with RBAC.
* PostgreSQL database with normalized schema.

#### Excluded Features:
* Real payment gateway integration (Sandbox only).
* Automated inventory reordering.
* Product reviews and ratings.
* Native push notifications.
* Multi-store inventory sharing (data is strictly isolated per vendor).

---

## 2.0 FUNCTIONAL REQUIREMENTS SPECIFICATION

### 2.1 Project Overview
* **Domain:** SaaS / Retail Management
* **Primary Users:** Software Owner (Admin) and Store Owner (Vendor).
* **Problem Statement:** Paper-based "listahan" systems are prone to error, lack backups, and cannot accept digital payments.
* **Solution:** A digital POS and credit tracker with secure isolation and integrated digital settlement.

### 2.2 Core User Journeys
1.  **Vendor Onboarding:** Google OAuth authentication, profile creation, inventory upload, and SMTP welcome email.
2.  **Point of Sale (Mobile):** JWT authentication, barcode scanning, real-time stock checks, and transaction recording.
3.  **Digital Credit Settlement:** Selection of customer from "Listahan," PayMongo Sandbox session, and automated email receipt.

### 2.3 Feature List (MoSCoW)
* **MUST HAVE:** JWT/OAuth Security, Product CRUD, File Upload, External Currency API, SMTP, PayMongo Sandbox.
* **SHOULD HAVE:** Daily sales analytics, low-stock indicators, customer credit history logs.
* **COULD HAVE:** Real-time stock alerts (WebSockets), barcode generation.
* **WON'T HAVE:** Real-money payments, push notifications, automated procurement.

### 2.4 Acceptance Criteria (Examples)
* **AC-1 (Multi-Tenant):** Vendors must receive a `403 Forbidden` if attempting to access "Manage All Vendors" endpoints.
* **AC-2 (Atomic Transactions):** Stock deduction must be atomic; if an order fails, stock must remain unchanged.

---

## 3.0 NON-FUNCTIONAL REQUIREMENTS

### 3.1 Performance
* **API Response Time:** ≤ 2 seconds for 95% of requests.
* **Database Efficiency:** Complex queries must complete within 500ms.
* **Mobile Cold Start:** Interactive within 3 seconds.

### 3.2 Security
* **Identity:** Mandatory JWT; tokens stored in `EncryptedSharedPreferences` (Android) and `localStorage` (React).
* **Isolation:** Every query must be scoped by `vendor_id`.
* **Encryption:** Passwords hashed via BCrypt; HTTPS/TLS for all data in transit.

### 3.3 Compatibility
* **Mobile:** Android API Level 34 (Android 14).
* **Web:** Latest versions of Chrome, Firefox, Safari, and Edge.
* **Backend:** Spring Boot 3.x.

---

## 4.0 SYSTEM ARCHITECTURE

### 4.1 Technology Stack
* **Backend:** Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA.
* **Database:** PostgreSQL 14+.
* **Web Frontend:** React 18, TypeScript, Tailwind CSS, Axios.
* **Mobile:** Kotlin, Jetpack Compose, Retrofit, Room.
* **Deployment:** Railway/Heroku (Backend), Vercel/Netlify (Web).

---

## 5.0 API CONTRACT & COMMUNICATION

### 5.1 Standards
* **Base URL:** `https://[server_hostname]:[port]/api/v1`
* **Format:** JSON
* **Auth:** Bearer Token (JWT)

### 5.2 Common Error Codes
* `AUTH-001`: Invalid credentials
* `VALID-001`: Validation failed
* `BUSINESS-001`: Insufficient stock
* `SYSTEM-001`: Internal server error

---

## 6.0 DATABASE DESIGN

### 6.1 Key Tables
* **users:** id, email, password_hash, full_name, role_id.
* **products:** id, vendor_id, name, barcode, price, stock_quantity.
* **orders:** id, vendor_id, customer_id, total_amount.
* **customers:** id, vendor_id, full_name, current_debt.

---

## 7.0 UI/UX DESIGN

* **Design System:** Primary Color `#0D9488`, Secondary Color `#FB923C`.
* **Typography:** Inter font family.
* **Mobile Focus:** 48x48dp minimum touch targets, offline caching for images, and bottom navigation.

---

## 8.0 PLAN & TIMELINE

* **Phase 1 (Week 1-2):** Planning, Architecture, and UI/UX Wireframes.
* **Phase 2 (Week 3-4):** Backend Development (Auth, CRUD, Cart, Orders).
* **Phase 3 (Week 5-6):** Web Application Development.
* **Phase 4 (Week 7-8):** Mobile Application Development.
* **Phase 5 (Week 9-10):** Integration, Testing, and Deployment.

**Milestone:** Full system deployment and integration by the end of Week 10.
