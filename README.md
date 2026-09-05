# FinCommerce - Full-Stack FinTech & E-Commerce Application

**FinCommerce** is a modern, secure, responsive full-stack web application that combines a **Digital FinTech Wallet** with a complete **E-Commerce Shopping Platform**.

---

## Key Modules & Features

### 1. Digital FinTech Wallet
- **Live Balance Overview**: Real-time display of Total Wallet Balance, Available Balance, Total Spent, and Monthly Spending.
- **Add Money**: Deposit funds into wallet via simulated UPI, Debit Card, Credit Card, or Net Banking gateways.
- **Withdraw Money**: Transfer wallet balance to linked bank accounts with Transaction PIN verification.
- **Send & Receive Money**: P2P transfers using Email, Mobile Number, or UPI ID.
- **UPI & QR Payments**: Generate dynamic QR codes, share payment links, and execute instant UPI payments.
- **Bank Account Linking**: Add, list, and manage bank accounts with masked account numbers for privacy.
- **Utility Bill Payments**: Pay Electricity, Water, Mobile Recharge, Broadband, Gas, DTH, Insurance, and Credit Card bills.

### 2. E-Commerce Platform
- **Storefront & Catalog**: Search products, filter by 8 categories (Electronics, Fashion, Grocery, Beauty, Home, Sports, Books, Accessories), and sort by price or rating.
- **Product Details & Reviews**: Multi-angle image views, specifications, stock availability, star ratings, and verified customer reviews.
- **Wishlist & Shopping Cart**: Manage saved items, adjust item quantities, and apply discount coupons (`WELCOME10`, `SAVE500`).
- **Multi-Step Checkout**: Address Selection $\rightarrow$ Order Summary $\rightarrow$ Payment Execution (Wallet, UPI, Card, Net Banking, Cash on Delivery) $\rightarrow$ Order Confirmation.
- **Visual Order Tracking**: Interactive step-by-step timeline tracking (Placed $\rightarrow$ Confirmed $\rightarrow$ Packed $\rightarrow$ Shipped $\rightarrow$ Out for Delivery $\rightarrow$ Delivered).
- **Returns & Instant Refunds**: Request order returns; upon approval, funds credit automatically back to the digital wallet balance.

### 3. Expense Tracker & Budget Management
- **Expense Analytics**: Category-based expense tracking (Food, Shopping, Travel, Bills, Entertainment, etc.).
- **Monthly Budgets**: Set category and total monthly spending limits with automatic visual threshold warnings when spending exceeds limits.

### 4. Rule-Based Fraud Engine & Security
- **Automated Fraud Detection**: Analyzes transactions for suspicious patterns (e.g. amounts over ₹20,000) and flags alerts.
- **Security Center**: Transaction PIN setup, Biometric login UI toggle, active session view, and security alert logs.

### 5. Admin Control Portal
- System overview metrics (Total Revenue, Total Users, Total Orders, Pending Fraud Alerts).
- Product Catalog CRUD & Stock Management.
- Order Fulfillment & Status Pipeline updates.
- Coupon Creation & Support Ticket Resolution.

---

## Technology Stack

- **Backend**: Java 21, Spring Boot 3.2+, Spring Web, Spring Data JPA, Spring Security 6, JWT Authentication, Maven.
- **Database**: PostgreSQL (Supabase Compatible) with automatic H2 fallback mode for zero-dependency local execution.
- **Frontend**: Responsive HTML5, CSS3 (Custom Design Tokens, Glassmorphism Dark/Light FinTech theme), ES6 JavaScript, FontAwesome 6.

---

## Project Structure

```
FinTech_app/
├── fincommerce/
│   ├── backend/
│   │   ├── pom.xml
│   │   ├── mvnw.cmd
│   │   └── src/main/java/com/fincommerce/
│   │       ├── config/        # SecurityConfig, DataInitializer
│   │       ├── controller/    # REST API Controllers
│   │       ├── dto/           # Request & Response DTOs
│   │       ├── entity/        # JPA Database Entities
│   │       ├── exception/     # Global Exception Handling
│   │       ├── repository/    # Spring Data Repositories
│   │       ├── security/      # JWT Authentication & Filters
│   │       └── service/       # Business Logic Services
│   └── frontend/
│       ├── index.html         # Landing Page
│       ├── login.html         # Sign In Page
│       ├── register.html      # Registration Page
│       ├── dashboard.html     # Unified User Dashboard
│       ├── wallet.html        # Digital Wallet Dashboard
│       ├── shop.html          # Storefront & Catalog
│       ├── product-details.html
│       ├── cart.html          # Shopping Cart
│       ├── checkout.html      # Multi-Step Checkout
│       ├── orders.html        # Orders & Visual Tracking
│       ├── transactions.html  # Full Transaction Ledger
│       ├── profile.html       # Profile & Address Book
│       ├── settings.html      # Security Center
│       ├── support.html       # Support & FAQ Chatbot
│       ├── admin.html         # Admin Portal
│       ├── css/               # Core & Layout CSS
│       └── js/                # API Client, Auth & UI Scripts
```

---

## Quick Start & Setup Instructions

### 1. Backend Setup
Navigate to `fincommerce/backend`:
```bash
cd fincommerce/backend
```

Run with Java / Maven:
```bash
mvn spring-boot:run
```
*(Or execute `mvnw.cmd` on Windows)*

The backend server starts on **`http://localhost:8085`**.

### 2. Frontend Setup
Open `fincommerce/frontend/index.html` or `login.html` in any web browser or use VS Code Live Server.

---

## Pre-seeded Demo Credentials

- **Admin Account**: `admin@fincommerce.com` / Password: `admin123` (Transaction PIN: `9999`)
- **Customer Account**: `john@example.com` / Password: `password123` (Transaction PIN: `1234`)
- **Active Coupons**: `WELCOME10` (10% OFF), `SAVE500` (₹500 OFF on ₹5000+)

---

## PostgreSQL & Supabase Database Configuration

To switch from the default local H2 in-memory mode to PostgreSQL / Supabase, update `fincommerce/backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://<SUPABASE_HOST>:5432/<DATABASE_NAME>
spring.datasource.username=postgres
spring.datasource.password=<YOUR_SUPABASE_PASSWORD>
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```
