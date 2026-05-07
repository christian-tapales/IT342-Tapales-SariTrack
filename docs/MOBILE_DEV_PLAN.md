# 📱 SariTrack Mobile Development Roadmap

This document tracks the progress of the Android Mobile application. It serves as the source of truth for features and integration steps.

---

## ✅ Phase 1: Foundation & Infrastructure (COMPLETED)
*Goal: Secure communication with the backend and session persistence.*

- [x] **Auth Layer**: Login and Register layouts with validation.
- [x] **API Connectivity**: Retrofit setup with `AuthInterceptor`.
- [x] **Session Management**: Persistent storage for JWT, Vendor ID, and Theme.
- [x] **Product CRUD**: Full ability to Add, Edit, and Delete products from mobile.
- [x] **Cloud Storage**: Integrated **Supabase** for mobile image uploads.

---

## 🏠 Phase 2: Unified Dashboard (COMPLETED)
*Goal: Create a data-driven home experience.*

- [x] **Header Fixes**: Resolved text overlap in the dashboard greeting.
- [x] **Navigation Cleanup**: Transitioned from 5-button to **3-button Bottom Nav** (Home, Products, Sales).
- [x] **Recent Activity**: Integrated Transaction History list into the Home screen for quick access.
- [x] **Stat Cards**: Dynamic cards for Today's Sales and Total Orders.

---

## 🛒 Phase 3: The POS Engine (The "Game Changer") (IN PROGRESS)
*Goal: Fast barcode scanning and transaction recording.*

- [x] **ML Kit Scanner**: High-speed scanning for EAN/UPC barcodes via BottomSheet.
- [x] **Cart Management**: Add multiple items to a "Cart" before finalizing the sale with real-time total calculation.
- [x] **Dual-Entry Scanner**: Added scanner shortcuts in `AddProductActivity` for faster inventory entry.
- [ ] **Checkout Flow**: 
    - [x] **Cash**: Immediate stock deduction and transaction recording.
    - [ ] **Utang**: Linking order to a Customer.
    - [ ] **Digital**: PayMongo Sandbox integration.

---

## 📒 Phase 4: Listahan (Debt Management)
*Goal: Manage customer credit on the go.*

- [x] **Customer Hub**: RecyclerView displaying all store debtors and balances.
- [ ] **Debt History**: View specific transaction records per customer.
- [x] **Settlement Flow**: Record payments to reduce customer debt via PaymentBottomSheet.

---

## ✨ Phase 5: Polish & UX
*Goal: App stability and premium feel.*

- [ ] **Theme Switching**: Finalize persistent Dark Mode across all activities.
- [ ] **Smooth Transitions**: Fragment transitions and shimmering loading states.
- [x] **Offline Guard**: Defensive checks and try-catch blocks to prevent crashes during server/data errors.

---

## 📈 Current Progress: **88%**
*Last Updated: 2026-05-07*
*Focusing on: **Phase 3: Checkout Flow (Utang) & Phase 4: Debt History***
