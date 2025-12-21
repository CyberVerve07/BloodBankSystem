A desktop-based Blood Bank Management System built using Java Swing that allows efficient management of blood donors with persistent storage, validation, and a clean user interface.

This project is designed for college-level projects, resume showcasing, and core Java practice, focusing on OOP concepts, Swing UI, and file handling.

📌 Features

Add new blood donors with complete details

Store donor information persistently (data is saved even after closing the app)

Update existing donor records

Delete donors safely using unique identifiers

Search donors by blood group

Input validation for:

Email format

Contact number (10 digits)

Duplicate donors (email & contact)

Clean and user-friendly UI

Single-file implementation (no modules, no packages)

🧾 Donor Details Stored

Each donor record includes:

Name

Father’s Name

Email Address

Blood Group

Contact Number

Auto-generated Unique ID (UUID – internal use)

🛠 Tech Stack

Language: Java

UI Framework: Java Swing

Data Storage: File-based persistence using Serialization

Core Concepts Used:

OOP (Classes, Objects, Encapsulation)

Collections (ArrayList)

Exception Handling

File I/O

Event Handling

UUID for safe record management


✅ Validation Rules

All fields are mandatory

Email must be in valid format

Contact number must be exactly 10 digits

Duplicate donors (same email or contact) are not allowed

🎯 Use Case

This application can be used as:

A college mini-project

A core Java + Swing practice project

A resume project demonstrating GUI + persistence

A base for further upgrades (database, web, REST API)

🚀 Possible Future Enhancements

MySQL / JDBC integration

JavaFX UI

Export donor list to CSV

Dark mode UI

Spring Boot backend with REST APIs
