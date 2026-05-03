# SariTrack 🛒

SariTrack is a full-stack system designed to streamline management for sari-sari stores. It provides a centralized backend with dual-client interfaces: a web-based management dashboard and a mobile application for on-the-go operations.

## 🏗️ System Architecture

The project is divided into three main modules:

1.  **`backend/`**: A Spring Boot REST API that handles data persistence, business logic, and security.
2.  **`web/`**: A React application (Vite) for store owners to manage inventory, view reports, and track sales.
3.  **`mobile/`**: A native Android application (Kotlin) for mobile store management.

---

## 🚀 Getting Started

### Prerequisites

- **Backend**: Java 17+, Maven (or use provided `mvnw`)
- **Web**: Node.js (v18+) and npm
- **Mobile**: Android Studio (Koala or later recommended)
- **Database**: PostgreSQL (already configured to connect to Supabase)

---

### 1. Running the Backend 🟢

The backend is a Spring Boot application.

1.  Navigate to the backend directory:
    ```bash
    cd backend
    ```
2.  Install dependencies:
    ```bash
    ./mvnw clean install
    ```
3.  Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```
The API will be available at `http://localhost:8080`.

---

### 2. Running the Web Client 🌐

The web client is built with React and Vite.

1.  Navigate to the web directory:
    ```bash
    cd web
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Run the development server:
    ```bash
    npm run dev
    ```
The application will be available at `http://localhost:5173`.

---

### 3. Running the Mobile Client 📱

The mobile client is a native Android project.

1.  Open **Android Studio**.
2.  Select **"Open"** and navigate to the `mobile/` directory.
3.  Wait for Gradle to sync.
4.  Connect an Android device or start an emulator.
5.  Click **"Run"** (Shift + F10).

---

## ✨ Features

-   **Inventory Management**: Track products, stock levels, and pricing.
-   **Sales Tracking**: Manage orders and transaction history.
-   **Customer Records**: Maintain profiles for store customers.
-   **Cross-Platform**: Access management tools from both web and mobile devices.
-   **Secure**: Centralized authentication and role-based access.

## 🛠️ Technology Stack

-   **Backend**: Spring Boot 3, Java 17, Spring Data JPA, Spring Security, PostgreSQL (Supabase).
-   **Frontend**: React 19, Vite, Tailwind CSS 4, Axios, React Router.
-   **Mobile**: Kotlin, Retrofit, Gson, Material Design.

## 📁 Project Structure

```text
IT342-Tapales-SariTrack/
├── backend/          # Spring Boot Application
├── web/              # React Web Application
├── mobile/           # Android Mobile Application
├── docs/             # Documentation and assets
└── README.md         # Project documentation
```

## 📝 License

This project is for educational purposes under the IT342 course - System Integration and Architecture.
