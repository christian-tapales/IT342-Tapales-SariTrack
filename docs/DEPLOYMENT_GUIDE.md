# 🚀 SariTrack Production Deployment & Cloud Architecture Guide

> **Architecture Philosophy:** Zero-Cost Cloud SaaS ($0/mo) with Resilient Microservice Design  
> **Course:** IT342 - System Integration and Architecture  
> **Author:** Christian Kyle Bayarcal Tapales  

---

## 1. 🌐 Cloud Topology & Cost Breakdown

SariTrack is engineered to operate on modern serverless and containerized cloud platforms at **$0/month total infrastructure cost**, while maintaining high availability, secure multi-tenancy, and global CDN delivery.

```mermaid
graph TD
    ClientWeb["Web Users / Recruiters"] -->|HTTPS (Global Anycast CDN)| Vercel["Vercel Edge Network (React 18 SPA)"]
    ClientMobile["Mobile POS / Phone"] -->|Retrofit HTTPS + JWT| Render["Render.com (Dockerized Spring Boot 3)"]
    Vercel -->|REST API Requests| Render
    
    subgraph "Cloud Backend & Storage"
        Render -->|PostgreSQL Wire (Port 6543)| SupabaseDB[("Supabase PostgreSQL Database")]
        Render -->|S3 REST API| SupabaseCDN["Supabase Storage Bucket ('products')"]
    end

    subgraph "High-Availability Layer ($0)"
        UptimeMonitor["UptimeRobot (Synthetic Heartbeat)"] -->|Ping /api/health every 10 min| Render
    end
```

### Monthly Cost Table
| Service | Provider | Allocation / Tier | Cost |
| :--- | :--- | :--- | :---: |
| **Frontend Web Hosting** | Vercel | Free Tier (Global Edge CDN, SSL, CI/CD) | **$0.00** |
| **Backend REST API** | Render | Free Tier (512MB RAM, 0.1 vCPU, 750 free hrs/mo) | **$0.00** |
| **Relational Database** | Supabase | Free Tier (500MB PostgreSQL, Connection Pooler) | **$0.00** |
| **Cloud Image Storage** | Supabase Storage | Free Tier (1GB Storage, Global CDN) | **$0.00** |
| **Synthetic Uptime Monitor** | UptimeRobot | Free Tier (50 Monitors, 5-min intervals) | **$0.00** |
| **Payment Gateway Sandbox** | PayMongo | Developer Sandbox (GCash, GrabPay, Cards) | **$0.00** |
| **Total Monthly Cost** | | | **$0.00 / month** |

---

## 2. 🛡️ High-Availability Strategy: Mitigating Free-Tier Cold Starts

### The Challenge
Free container platforms (like Render or Koyeb) spin down inactive containers after 15 minutes of zero traffic to conserve cloud computing resources. A Java/Spring Boot cold start can take 40–70 seconds, which can create a poor first impression if a recruiter or evaluator visits the website during sleep mode.

### The Architectural Solution
1. **Low-Memory JVM Optimization:**  
   Our [`backend/Dockerfile`](../backend/Dockerfile) applies specialized JVM flags for 512MB containers:
   ```dockerfile
   ENV JAVA_TOOL_OPTIONS="-Xmx350m -Xss512k -XX:+UseSerialGC -Dfile.encoding=UTF-8"
   ```
   This prevents Out-Of-Memory (OOM 137) container kills while maintaining snappy execution.
2. **Synthetic Keep-Alive Heartbeat:**  
   Render allocates **750 free instance hours per month** (a full 31-day month is only 744 hours). A single free service can legally run **24/7 without shutting down** as long as it receives periodic HTTP traffic.
   * **Configuration:** An automated HTTP monitor pings `GET https://saritrack-api.onrender.com/api/health` every 10 minutes.
   * **Result:** Zero cold starts. The API responds in under 200ms when any evaluator visits.
3. **Database Liveness Check:**  
   To prevent Supabase from pausing inactive databases after 7 days, a scheduled probe accesses `/api/health/ready` to verify and keep the PostgreSQL pooler warm.

---

## 3. 🔐 Security & Secrets Management (12-Factor App)

### Zero-Trust Repository Protocol
In adherence to the **Twelve-Factor App methodology**, **NO real credentials or API secrets are EVER stored in Git**:

1. **Version Control:** Only environment variable keys (e.g., `${SPRING_DATASOURCE_PASSWORD}`) or safe templates ([`.env.example`](../web/.env.example), [`application-local.properties.example`](../backend/src/main/resources/application-local.properties.example)) are committed.
2. **Cloud Vaults:** Secrets are injected exclusively at runtime via Render and Vercel's encrypted environment variable dashboards.
3. **Local Development:** Developers use `application-local.properties` and `web/.env`, both of which are strictly ignored by Git in [`.gitignore`](../.gitignore).

---

## 4. 📋 Step-by-Step Deployment Instructions

### Step 1: Deploy Backend on Render.com
1. Create a **New Web Service** connected to `christian-tapales/IT342-Tapales-SariTrack`.
2. Settings:
   - **Root Directory:** `backend`
   - **Environment:** `Docker`
   - **Region:** Singapore (`ap-southeast-1`)
3. Add Environment Variables (from your private notes, NOT committed to Git):
   - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
   - `PAYMONGO_SECRET_KEY`, `PAYMONGO_WEBHOOK_SECRET`
   - `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
   - `MAIL_USERNAME`, `MAIL_PASSWORD`
4. Deploy and note your URL (e.g., `https://saritrack-api.onrender.com`).

### Step 2: Set Up Keep-Alive Monitor (UptimeRobot)
1. In [UptimeRobot.com](https://uptimerobot.com), click **Add New Monitor**.
2. **Monitor Type:** `HTTP(s)`
3. **Friendly Name:** `SariTrack API Keep-Alive`
4. **URL:** `https://saritrack-api.onrender.com/api/health`
5. **Monitoring Interval:** Every `10 minutes`

### Step 3: Deploy Web on Vercel
1. In [Vercel.com](https://vercel.com), import `IT342-Tapales-SariTrack`.
2. Set **Root Directory** to `web`.
3. Set Environment Variables:
   - `VITE_API_URL` = `https://saritrack-api.onrender.com`
   - `VITE_SUPABASE_URL` = `https://your-ref.supabase.co`
   - `VITE_SUPABASE_ANON_KEY` = `your-anon-key`
4. Click **Deploy**.

### Step 4: Configure Android POS Mobile App
1. In Android Studio, open `RetrofitClient.kt`.
2. Update `BASE_URL` to `https://saritrack-api.onrender.com/`.
3. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)** to generate the installer for demonstration devices.
