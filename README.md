# 🩸 Blood Bank Management System (Full-Stack)

A modern, high-performance Blood Bank Management System built with a decoupled architecture. This system features a robust **Spring Boot REST API** with JWT authentication and a premium, responsive **React** frontend designed with **Glassmorphism** aesthetics.

---

## ✨ Key Features

### 🔐 Secure Access
- **JWT Authentication:** Stateless, secure API communication.
- **Role-Based Access (RBAC):** Separate workflows for **Admin**, **Staff**, and **Donor**.
- **Automated Data Seeding:** System initializes with default users and inventory.

### 🩸 Blood Management
- **Real-time Inventory:** Track blood stock levels across all blood groups (A+, O-, etc.).
- **Smart Eligibility Logic:** Automated 90-day waiting period check for donors to ensure health safety.
- **Emergency Requests:** Hospital request workflow with priority levels (Critical, High, Normal).
- **Auto-Inventory Updates:** Inventory levels automatically adjust when donations are fulfilled or requests are processed.

### 📊 Modern Dashboard
- **Visual Analytics:** Interactive charts for blood distribution and inventory levels.
- **Live Stats:** Quick view of total donors, donations, and pending requests.
- **Glassmorphism UI:** Premium, dark-themed design with smooth animations.

---

## 🛠️ Technology Stack

### **Backend (Java)**
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security & JSON Web Tokens (JWT)
- **Database:** H2 Database (In-Memory for demo) / JPA Hibernate
- **Build Tool:** Maven

### **Frontend (React)**
- **Build Tool:** Vite
- **Styling:** Vanilla CSS (Custom Glassmorphism) & Tailwind CSS
- **Icons:** Lucide React
- **Animations:** Framer Motion
- **Charts:** Chart.js with React-Chartjs-2
- **API Client:** Axios (with Interceptors)

---

## 🚀 Getting Started

### Prerequisites
- **Java 17 or higher**
- **Node.js 18 or higher**
- **Maven** (optional, wrapper included)

### 1. Run the Backend
```bash
cd backend
./mvnw spring-boot:run
```
*The API will start on `http://localhost:8080`*

### 2. Run the Frontend
```bash
cd frontend-react
npm install
npm run dev
```
*The UI will start on `http://localhost:5173`*

---

## 🔑 Demo Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` |
| **Staff** | `staff` | `staff123` |

---

## 📂 Project Structure
```text
BloodBankSystem/
├── backend/               # Spring Boot REST API
│   ├── src/main/java/     # Core logic, Entities, Controllers
│   └── src/main/resources # Config & Properties
├── frontend-react/        # React Application
│   ├── src/pages/         # Page components (Dashboard, Donors, etc.)
│   ├── src/components/    # Reusable UI elements
│   └── src/api.js         # Axios client configuration
└── README.md              # Project Documentation
```

---

## 🛡️ License
This project is for educational and practical use in blood bank management scenarios.

---
*Built with ❤️ for saving lives.*
