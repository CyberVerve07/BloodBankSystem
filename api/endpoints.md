# Blood Bank System - API Documentation

This project follows a RESTful architecture for donor and donation management.

## 1. Donor Management

### Get All Donors
- **URL:** `/donors`
- **Method:** `GET`
- **Query Params:** `bloodGroup` (Optional)
- **Description:** Returns a list of all donors or filtered by blood group.

### Add New Donor
- **URL:** `/donors/add`
- **Method:** `POST`
- **Body:** `Donor` object (Name, Email, Blood Group, Contact, etc.)
- **Description:** Registers a new donor in the system.

### Edit Donor
- **URL:** `/donors/edit/{id}`
- **Method:** `POST`
- **Description:** Updates details of an existing donor.

### Delete Donor
- **URL:** `/donors/delete/{id}`
- **Method:** `GET`
- **Description:** Removes a donor record.

## 2. Donation Management

### View Donor Donations
- **URL:** `/donations/donor/{id}`
- **Method:** `GET`
- **Description:** Returns donation history for a specific donor.

### Add Donation
- **URL:** `/donations/add/{id}`
- **Method:** `POST`
- **Description:** Records a new blood donation for a donor.

---
*Note: This system currently uses SSR with Thymeleaf, but the controllers are ready to be converted to @RestController for JSON responses.*
