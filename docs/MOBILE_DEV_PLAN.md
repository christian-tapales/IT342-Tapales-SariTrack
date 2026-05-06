# 📱 SariTrack Mobile Development Roadmap

This document tracks the progress of the Android Mobile application. It serves as the source of truth for features and integration steps.

---

## 🛠️ Phase 1: Foundation & Infrastructure
*Goal: Secure communication with the backend and session persistence.*

- [x] **Basic Auth Screens**: Login and Register layouts.
- [x] **API Connectivity**: Basic Retrofit setup.
- [ ] **Session Manager**: Implement `EncryptedSharedPreferences` to persist the JWT token.
- [ ] **OkHttp Interceptor**: Automatic `Authorization: Bearer <token>` header injection.
- [ ] **Base Data Models**: Kotlin data classes for `User`, `Product`, `Customer`, and `Order`.

---

## 📦 Phase 2: Core Data Layer
*Goal: Implement services for all backend resources.*

- [ ] **ProductApiService**: Fetch inventory and search by barcode.
- [ ] **CustomerApiService**: Fetch customer list and balance.
- [ ] **OrderApiService**: Submit sales and record debt.
- [ ] **Image Loading**: Integrate **Glide** for product image rendering.

---

## 🛒 Phase 3: Inventory & Look-up
*Goal: Allow vendors to browse their store data.*

- [ ] **InventoryActivity**: RecyclerView with search and category filtering.
- [ ] **Product Details**: View specific product info and stock history.
- [ ] **Dashboard Enhancement**: Quick stats (Today's Sales, Low Stock count).

---

## 🔍 Phase 4: Mobile POS (The "Game Changer")
*Goal: Fast barcode scanning and transaction recording.*

- [ ] **Camera Integration**: Setup **CameraX** for barcode detection.
- [ ] **ML Kit Integration**: High-speed scanning for EAN/UPC barcodes.
- [ ] **Cart State**: Local logic to manage multiple items before checkout.
- [ ] **Checkout Flow**: 
    - [ ] **Cash**: Immediate stock deduction.
    - [ ] **Utang**: Linking order to a Customer.
    - [ ] **Digital**: Redirect to PayMongo Sandbox.

---

## 📒 Phase 5: Listahan (Debt Management)
*Goal: Manage customer credit on the go.*

- [ ] **Customer Selection**: Easy lookup during the POS checkout.
- [ ] **Debt History**: View a customer's past transactions and total balance.
- [ ] **Payment Recording**: Allow customers to pay off debt via the mobile app.

---

## ✨ Phase 6: Polish & Security
*Goal: App stability and UX improvements.*

- [ ] **Token Expiry Handling**: Auto-redirect to login if the session expires.
- [ ] **Loading States**: Add shimmering effects and progress indicators.
- [ ] **Offline Cache**: Basic caching of product names for offline searching.

---

## 📈 Current Progress: **15%**
*Last Updated: 2026-05-07*
*Focusing on: **Phase 1 (Session Management & Networking)***
