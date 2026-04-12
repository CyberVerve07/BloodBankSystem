A full-stack Blood Bank Management System built using Spring Boot that allows efficient management of blood donors with database storage, validation, and a web-based user interface.

This project is designed for college-level projects, resume showcasing, and full-stack Java development practice, focusing on Spring Boot, JPA, Thymeleaf, and database integration.

📌 Features

Add new blood donors with complete details

Store donor information in H2 database (persistent across restarts)

Update existing donor records

Delete donors safely

Search donors by blood group

Input validation for:

Email format

Contact number (10 digits)

Duplicate donors (email & contact)

Web-based user interface using Thymeleaf

RESTful architecture with MVC pattern

🧾 Donor Details Stored

Each donor record includes:

Name

Father’s Name

Email Address

Blood Group

Contact Number

Auto-generated Unique ID

🛠 Tech Stack

Language: Java 17

Framework: Spring Boot

Web Framework: Spring MVC

Template Engine: Thymeleaf

Database: H2 (in-memory, with console access)

ORM: JPA/Hibernate

Validation: Bean Validation

Build Tool: Maven

Core Concepts Used:

Spring Dependency Injection

JPA Repositories

Thymeleaf Templates

MVC Architecture

Bean Validation

Exception Handling

Database Transactions

✅ Validation Rules

All fields are mandatory

Email must be in valid format

Contact number must be exactly 10 digits

Duplicate donors (same email or contact) are not allowed

🎯 Use Case

This application can be used as:

A full-stack Java web development project

A Spring Boot practice project

A resume project demonstrating web development, database integration, and validation

A base for further enhancements (authentication, REST APIs, microservices)

🚀 Getting Started

Prerequisites:

Java 17 or higher

Maven 3.6+

Steps:

1. Clone the repository

2. Navigate to the project directory

3. Run `mvn clean install`

4. Run `mvn spring-boot:run`

5. Open browser and go to http://localhost:8080/donors

H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb, username: sa, password: )

🚀 Possible Future Enhancements

User authentication and authorization

REST API endpoints for mobile apps

MySQL/PostgreSQL integration

Frontend with React/Angular

Docker containerization

CI/CD pipeline
