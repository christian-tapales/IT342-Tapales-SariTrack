# SariTrack System Design Document (SDD) - REVISED

**Project Title:** SariTrack  
**Author:** Christian Kyle Bayarcal Tapales  
**Version:** 1.1 (As-Implemented)  
**Date:** May 8, 2026  
**Status:** Final

---

## REVISION HISTORY

| Version | Date | Author | Changes Made | Status |
| :--- | :--- | :--- | :--- | :--- |
| 1.0 | February 23, 2026 | Christian Kyle Tapales | Baseline version for development | Approved |
| 1.1 | May 8, 2026 | Christian Kyle Tapales | Updated to reflect as-built tech stack (JS/XML) and final features. | Final |

---

## 1.0 EXECUTIVE SUMMARY

### 1.1 Project Overview
SariTrack is a multi-tenant SaaS platform designed to modernize the traditional sari-sari store ecosystem. The system provides a digital point-of-sale (POS) experience, featuring a **Spring Boot** backend, a **React** web dashboard for management, and an **Android** mobile application for real-time sales scanning.

### 1.2 Objectives
* Develop a multi-tenant management system with secure JWT authentication and RBAC.
* Implement a three-tier architecture: Spring Boot (Backend), React (Web Portal), and Android Kotlin/XML (Mobile POS).
* Create RESTful APIs integrating external services: PayMongo Sandbox, Currency Exchange APIs, and SMTP.
* Ensure strict data isolation between vendors and secure cloud storage for product images.

### 1.3 Scope (As Built)
* **User Management:** JWT-based auth with Google Account Picker integration.
* **Inventory:** Product CRUD with Supabase storage and barcode lookup.
* **POS System:** Cart management and transaction recording via Mobile.
* **Payments:** Digital credit settlement via PayMongo Sandbox.
* **Reporting:** Sales analytics, CSV/PDF Data Exports, and transaction history.
* **Admin Panel:** Global vendor monitoring and product management.

---

## 2.0 FUNCTIONAL REQUIREMENTS SPECIFICATION

### 2.3 Feature List (MoSCoW)
* **MUST HAVE (100%):** JWT Security, Product CRUD, File Upload (Supabase), External Currency API, SMTP, PayMongo Sandbox.
* **SHOULD HAVE (100%):** Daily sales analytics, low-stock indicators, customer credit history, **Professional PDF/CSV Exports**.
* **COULD HAVE:** Barcode generation, Real-time stock alerts (Future Roadmap).

---

## 4.0 SYSTEM ARCHITECTURE

### 4.1 Technology Stack (Actual)
* **Backend:** Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA.
* **Database:** PostgreSQL (Hosted on Supabase).
* **Web Frontend:** React 18, JavaScript (ES6+), Tailwind CSS 4, Axios.
* **Mobile:** Kotlin, XML Layouts (Classic View System), Retrofit 2, Room DB.
* **External Services:** Supabase Storage, PayMongo API, ExchangeRate API.

---

## 5.0 API CONTRACT & COMMUNICATION

### 5.1 Standards
* **Base URL:** `https://[server_hostname]/api`
* **Format:** JSON
* **Auth:** Bearer Token (JWT)

---

## 6.0 DATABASE DESIGN

### 6.1 Key Tables
* **users:** id, email, password_hash, full_name, role (ADMIN/VENDOR), created_at.
* **products:** id, vendor_id, name, barcode, price, stock_quantity, image_url.
* **orders:** id, vendor_id, total_amount, status (PAID/PENDING/CANCELLED), timestamp.
* **customers:** id, vendor_id, name, current_debt, contact_info.

---

## 7.0 UI/UX DESIGN

* **Design System:** Primary Color `#16A394` (Teal), Secondary Color `#FB923C` (Orange).
* **Premium Features:** Support for class-based **Dark Mode**, Skeleton loaders, and smooth UI micro-animations.
* **Mobile Focus:** Barcode-first entry, ML Kit integration for scanning, and bottom navigation.

---

## 8.0 FINAL STATUS
The SariTrack platform has successfully completed its primary development cycle. All "Must Have" and "Should Have" requirements are fully integrated and tested, achieving a functional readiness of 100%.
