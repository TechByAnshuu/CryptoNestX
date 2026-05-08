# CryptoNestX: Project Structure & Daily Commit Strategy

This document outlines the complete component-wise architecture of the `CryptoNestX` project and provides a strategic, step-by-step daily commit plan. By grouping exactly **3 files per day**, you can build a realistic and organized GitHub contribution history that mimics a natural Agile development cycle.

---

## 🏗️ 1. Component-Wise Project Structure

The project is structured as a **Microservices Monorepo**. Here is the high-level map of the components and their critical files:

### ⚙️ Root Infrastructure
Manages the orchestration, global dependencies, and environment variables.
* `pom.xml` (Parent Maven aggregator)
* `docker-compose.yml` (Container orchestration)
* `.gitignore` (Global git rules)

### 🔐 Auth Service (`auth-service/`)
Handles user registration, login, and JWT token issuance.
* `pom.xml` / `Dockerfile`
* `src/main/resources/application.yml`
* `src/main/java/.../AuthServiceApplication.java`
* `src/main/java/.../entity/User.java`
* `src/main/java/.../entity/Role.java`
* `src/main/java/.../security/JwtTokenProvider.java`
* `src/main/java/.../controller/AuthController.java`

### 📈 Portfolio Service (`portfolio-service/`)
Manages user balances, assets, and P&L calculations.
* `pom.xml` / `Dockerfile`
* `src/main/resources/application.yml`
* `src/main/java/.../PortfolioServiceApplication.java`
* `src/main/java/.../entity/Portfolio.java`
* `src/main/java/.../controller/PortfolioController.java`

### 💱 Exchange Service (`exchange-service/`)
Handles order execution, live market prices, and order books.
* `pom.xml` / `Dockerfile`
* `src/main/resources/application.yml`
* `src/main/java/.../ExchangeServiceApplication.java`
* `src/main/java/.../entity/Order.java`
* `src/main/java/.../service/OrderService.java`

### 🔔 Notification Service (`notification-service/`)
Listens to Kafka events and sends email alerts.
* `pom.xml` / `Dockerfile`
* `src/main/resources/application.yml`
* `src/main/java/.../NotificationServiceApplication.java`
* `src/main/java/.../kafka/OrderEventConsumer.java`
* `src/main/java/.../service/EmailService.java`

### 🖥️ Frontend (React Dashboard) (`frontend/`)
The Bugatti-inspired UI for trading and tracking.
* `package.json` / `vite.config.ts` / `Dockerfile` / `nginx.conf`
* `src/index.css` (Design System)
* `src/App.tsx` / `src/main.tsx` (Routing & Entry)
* **Components:** `src/components/TopNav.tsx`, `src/components/Footer.tsx`
* **Pages:** `src/pages/Home.tsx`, `src/pages/Login.tsx`, `src/pages/Dashboard.tsx`, `src/pages/Portfolio.tsx`, `src/pages/Exchange.tsx`

---

## 📅 2. Daily Commit Strategy (3 Files Per Day)

To simulate a realistic, steady pace of development, here is how you should stage and commit your files to GitHub. 

**Instructions for each day:**
1. Open terminal in the project root.
2. Run `git add <File 1> <File 2> <File 3>` using the exact paths listed below.
3. Run `git commit -m "Your commit message"`
4. Run `git push origin main`

### 🔵 Phase 1: Project Skeleton & Foundation

**Day 1: Monorepo Setup**
* `pom.xml` (Root)
* `.gitignore`
* `docker-compose.yml`
> *Commit Message:* `chore: initialize monorepo structure and docker orchestration`

**Day 2: Authentication Base**
* `auth-service/pom.xml`
* `auth-service/Dockerfile`
* `auth-service/src/main/resources/application.yml`
> *Commit Message:* `feat(auth): bootstrap authentication service config`

**Day 3: Auth Entities & Security**
* `auth-service/src/main/java/com/cryptonest/auth/AuthServiceApplication.java`
* `auth-service/src/main/java/com/cryptonest/auth/entity/User.java`
* `auth-service/src/main/java/com/cryptonest/auth/entity/Role.java`
> *Commit Message:* `feat(auth): create user and role JPA entities`

**Day 4: Auth Controllers**
* `auth-service/src/main/java/com/cryptonest/auth/security/JwtTokenProvider.java`
* `auth-service/src/main/java/com/cryptonest/auth/controller/AuthController.java`
* `auth-service/src/main/java/com/cryptonest/auth/service/UserService.java`
> *Commit Message:* `feat(auth): implement JWT issuance and login endpoints`

### 🟢 Phase 2: Core Microservices

**Day 5: Portfolio Base**
* `portfolio-service/pom.xml`
* `portfolio-service/Dockerfile`
* `portfolio-service/src/main/resources/application.yml`
> *Commit Message:* `feat(portfolio): initialize portfolio service configuration`

**Day 6: Portfolio Logic**
* `portfolio-service/src/main/java/com/cryptonest/portfolio/PortfolioServiceApplication.java`
* `portfolio-service/src/main/java/com/cryptonest/portfolio/entity/Portfolio.java`
* `portfolio-service/src/main/java/com/cryptonest/portfolio/controller/PortfolioController.java`
> *Commit Message:* `feat(portfolio): implement asset tracking endpoints`

**Day 7: Exchange Base**
* `exchange-service/pom.xml`
* `exchange-service/Dockerfile`
* `exchange-service/src/main/resources/application.yml`
> *Commit Message:* `feat(exchange): bootstrap exchange service environment`

**Day 8: Exchange Logic**
* `exchange-service/src/main/java/com/cryptonest/exchange/ExchangeServiceApplication.java`
* `exchange-service/src/main/java/com/cryptonest/exchange/entity/Order.java`
* `exchange-service/src/main/java/com/cryptonest/exchange/service/OrderService.java`
> *Commit Message:* `feat(exchange): implement order matching logic`

### 🟠 Phase 3: Events & Notifications

**Day 9: Notification Base**
* `notification-service/pom.xml`
* `notification-service/Dockerfile`
* `notification-service/src/main/resources/application.yml`
> *Commit Message:* `feat(notify): setup notification service configs`

**Day 10: Kafka & Email Integration**
* `notification-service/src/main/java/com/cryptonest/notification/NotificationServiceApplication.java`
* `notification-service/src/main/java/com/cryptonest/notification/kafka/OrderEventConsumer.java`
* `notification-service/src/main/java/com/cryptonest/notification/service/EmailService.java`
> *Commit Message:* `feat(notify): implement kafka event listener and SMTP service`

### 🟣 Phase 4: Frontend Development

**Day 11: React Setup & Config**
* `frontend/package.json`
* `frontend/vite.config.ts`
* `frontend/nginx.conf`
> *Commit Message:* `chore(ui): initialize react vite app and nginx config`

**Day 12: Design System & Core Routing**
* `frontend/src/index.css`
* `frontend/src/App.tsx`
* `frontend/src/main.tsx`
> *Commit Message:* `feat(ui): implement bugatti design system and routing`

**Day 13: Shared Components & Landing**
* `frontend/src/components/TopNav.tsx`
* `frontend/src/components/Footer.tsx`
* `frontend/src/pages/Home.tsx`
> *Commit Message:* `feat(ui): build navigation and landing page`

**Day 14: Authentication & Dashboard**
* `frontend/src/pages/Login.tsx`
* `frontend/src/pages/Dashboard.tsx`
* `frontend/Dockerfile`
> *Commit Message:* `feat(ui): create login and main analytics dashboard`

**Day 15: Trading & Portfolio UI**
* `frontend/src/pages/Portfolio.tsx`
* `frontend/src/pages/Exchange.tsx`
* `README.md`
> *Commit Message:* `feat(ui): implement live trading view and portfolio tracker`

---
*Note: Any additional files (like DTOs or Repositories) can be seamlessly mixed into their respective microservice's days as needed!*
