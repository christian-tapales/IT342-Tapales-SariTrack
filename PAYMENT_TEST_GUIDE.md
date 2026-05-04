# 💳 PayMongo Integration Test Guide - SariTrack

This document explains how to test the digital payment integration for the SariTrack System.

## 🛠 Prerequisites
1.  **Backend:** Must be running on `localhost:8080`.
2.  **Ngrok:** A tunnel must be active: `ngrok http 8080`.
3.  **Webhook:** The ngrok URL must be registered in the PayMongo Dashboard with the path `/api/webhooks/paymongo`.
4.  **Keys:** `paymongo.secret.key` and `paymongo.webhook.secret` must be correctly set in `application.properties`.

## 🚀 How to Test
1.  **Login:** Access the vendor dashboard.
2.  **POS:** Navigate to the "Sales" page.
3.  **Cart:** Add products to your cart.
4.  **Checkout:** Click **"Pay via Digital Wallet"**.
5.  **Redirect:** You will be sent to the PayMongo Secure Checkout.
6.  **Payment:**
    *   Select **GCash**.
    *   Click **Continue**.
    *   Use Test Number: `09000000000`.
    *   Use any 6-digit OTP (e.g., `123456`).
    *   Click **Authorize Test Payment**.
7.  **Confirmation:** You will be redirected back to the SariTrack Success page.

## 📊 Verification (SIA Logic)
*   **Database:** The order status in the `orders` table will change from `PENDING` to `PAID`.
*   **Inventory:** The stock quantity for the items purchased will only decrease **after** the payment is successful (handled via Webhook).
*   **Webhook Logs:** Check the backend terminal for `--- PAYMONGO WEBHOOK RECEIVED ---`.

---
*Created for IT342 - System Integration and Architecture Final Project.*
