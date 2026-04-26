# 🩸 Blood Bank Management System

A premium, full-stack management system for Blood Banks, built with **Spring Boot 3**, **Thymeleaf**, and **Modern JavaScript**.

## 🌟 Features
- **Donor Management:** Add, edit, delete, and search donors.
- **Donation Tracking:** Keep a history of every life-saving contribution.
- **Interactive Dashboard:** Visual analytics using Chart.js.
- **Live Search:** Real-time filtering of records.
- **Blood Compatibility Tool:** Smart tool to check donor/recipient compatibility.
- **Glassmorphism UI:** Premium, modern aesthetics with a deep crimson theme.

## 🏗 Project Structure
- `backend/`: Core Spring Boot application (Java, JPA, H2).
- `frontend/`: UI Layer (Thymeleaf templates & CSS/JS).
- `api/`: API Endpoint documentation.
- `documentation/`: Project guides and resources.

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven (Included in `backend/apache-maven-3.9.6`)

### Installation & Run
1. Navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Build the project:
   ```bash
   ./apache-maven-3.9.6/bin/mvn clean install
   ```
3. Run the application:
   ```bash
   ./apache-maven-3.9.6/bin/mvn spring-boot:run
   ```
4. Open your browser:
   - **App:** `http://localhost:8080/`
   - **Database Console:** `http://localhost:8080/h2-console`

## 🛠 Tech Stack
- **Backend:** Java 17, Spring Boot, Spring Data JPA, H2 Database.
- **Frontend:** HTML5, CSS3 (Glassmorphism), JavaScript (ES6+), Chart.js.
- **Template Engine:** Thymeleaf.

---
*Created with ❤️ for a better humanity.*
