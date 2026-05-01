# Customer Management System

A full-stack Customer Management System built with Spring Boot and React JS.
This system allows you to create, update, view, and manage customers,
including bulk import via Excel file.

---

## Technologies Used

### Backend
| Technology        | Version  | Purpose                    |
|-------------------|----------|----------------------------|
| Java              | 17       | Programming Language        |
| Spring Boot       | 3.2.5    | Backend Framework           |
| Spring Data JPA   | 3.2.5    | Database ORM                |
| MariaDB           | 11.x     | Database                    |
| Apache POI        | 5.2.5    | Excel File Processing       |
| Maven             | 3.6+     | Build Tool                  |
| JUnit 5           | 5.x      | Unit Testing                |
| Lombok            | 1.18.x   | Reduce Boilerplate Code     |
| Hibernate         | 6.x      | ORM Framework               |

### Frontend
| Technology        | Version  | Purpose                    |
|-------------------|----------|----------------------------|
| React JS          | 18.x     | Frontend Framework          |
| React Router DOM  | 6.x      | Page Navigation             |
| Axios             | 1.x      | HTTP Client (API calls)     |
| React DatePicker  | 4.x      | Date Selection Component    |

---

## Features

- ✅ **Create Customer** - Add new customers with all details
- ✅ **Update Customer** - Edit existing customer information
- ✅ **View Customer** - View detailed customer profile
- ✅ **Customer List** - View all customers in table format with search
- ✅ **Multiple Mobile Numbers** - Each customer can have multiple numbers
- ✅ **Multiple Addresses** - Each customer can have multiple addresses
- ✅ **Family Members** - Link customers as family members
- ✅ **Bulk Import** - Upload Excel file to create multiple customers
- ✅ **Master Data** - Cities and Countries managed in database
- ✅ **Input Validation** - Frontend and backend validation
- ✅ **Error Handling** - Global exception handling
- ✅ **Modern UI** - Clean and responsive design

---

## Project Structure

<img width="618" height="356" alt="image" src="https://github.com/user-attachments/assets/71a1223d-6d47-404d-b751-7ae45a2f6fd4" />


---

## Prerequisites

Before running this project, make sure you have installed:

| Software    | Version  | Download Link                              |
|-------------|----------|--------------------------------------------|
| Java JDK    | 17+      | https://adoptium.net/                      |
| Maven       | 3.6+     | https://maven.apache.org/download.cgi      |
| Node.js     | 18+      | https://nodejs.org/                        |
| MariaDB     | 11.x     | https://mariadb.org/download/              |
| Git         | Latest   | https://git-scm.com/downloads              |

---

## Setup Instructions

### Step 1: Database Setup

#### 1.1 Start MariaDB Service
**Windows:**
```bash
net start mariadb
Mac:

Bash

brew services start mariadb
Linux:

Bash

sudo systemctl start mariadb
1.2 Login to MariaDB
Bash

mysql -u root -p
1.3 Create Database
SQL

CREATE DATABASE IF NOT EXISTS customerdb;
EXIT;
1.4 Run DDL Script (Create Tables)
Open MySQL Workbench or run:

Bash

mysql -u root -p customerdb < DDL.sql
1.5 Run DML Script (Insert Sample Data)
Bash

mysql -u root -p customerdb < DML.sql
1.6 Verify Data
SQL

USE customerdb;
SHOW TABLES;
SELECT COUNT(*) FROM customer;
SELECT COUNT(*) FROM country;
SELECT COUNT(*) FROM city;
Expected output:

text

Tables: address, city, country, customer, customer_family, mobile_number
Customers: 10
Countries: 10
Cities: 35

Step 2: Backend Setup
2.1 Navigate to Backend Folder
cd customer_management_system

2.2 Update Database Credentials
Open this file:
src/main/resources/application.properties
Update these values:

properties

spring.datasource.url=jdbc:mariadb://localhost:3306/customerdb
spring.datasource.username=root
spring.datasource.password=YOUR_MARIADB_PASSWORD

2.3 Build the Project
mvn clean install -DskipTests

2.4 Run the Application
mvn spring-boot:run

2.5 Verify Backend is Running
Open browser and go to:
http://localhost:8080/api/master/countries
You should see a JSON response with countries list.

Step 3: Frontend Setup
3.1 Open New Terminal

3.2 Navigate to Frontend Folder
cd customer-frontend

3.3 Install Dependencies
npm install

3.4 Start the Application
npm start

3.5 Verify Frontend is Running
Browser will automatically open:
http://localhost:3000

Running Both Applications
You need two terminals running at the same time:

Terminal 1 - Backend:
cd customer_management_system
mvn spring-boot:run

Terminal 2 - Frontend:
cd customer-frontend
npm start

Application	URL	                    Status
Backend	        http://localhost:8080	Must be first
Frontend	http://localhost:3000	Open in browser
Database	localhost:3306	        Must be running

API Documentation

Customer Endpoints
Method	  Endpoint	                    Description	                Request Body
GET	      /api/customers	            Get all customers	        None
POST	  /api/customers	            Create new customer  	    CustomerRequest JSON
GET	      /api/customers/{id}	        Get customer by ID	        None
PUT       /api/customers/{id}	        Update customer	            CustomerRequest JSON
DELETE	  /api/customers/{id}	        Delete customer     	    None
POST	  /api/customers/bulk-import	Bulk import from Excel	    Multipart file

Master Data Endpoints
Method	  Endpoint	                        Description
GET	      /api/master/countries	            Get all countries
GET	      /api/master/cities	            Get all cities
GET	      /api/master/cities/country/{id}	Get cities by country

Sample Request Body (Create Customer)


{
    "name": "John Doe",
    "dateOfBirth": "1990-01-15",
    "nicNumber": "199012345678",
    "mobileNumbers": [
        "0771234567",
        "0712345678"
    ],
    "addresses": [
        {
            "addressLine1": "No 123, Main Street",
            "addressLine2": "Colombo 03",
            "cityId": 1,
            "countryId": 1
        }
    ],
    "familyMemberIds": [2, 3]
}

Sample Response (Customer)

{
    "id": 1,
    "name": "John Doe",
    "dateOfBirth": "1990-01-15",
    "nicNumber": "199012345678",
    "mobileNumbers": [
        "0771234567",
        "0712345678"
    ],
    "addresses": [
        {
            "id": 1,
            "addressLine1": "No 123, Main Street",
            "addressLine2": "Colombo 03",
            "cityName": "Colombo",
            "countryName": "Sri Lanka"
        }
    ],
    "familyMembers": [
        {
            "id": 2,
            "name": "Jane Smith",
            "nicNumber": "198556789012"
        }
    ]
}

Excel Import Format
Required Columns
Column	  Header	        Required	Format	        Example
A	      Name	            Yes	        Text	        Kalana Heshan
B	      Date of Birth	    Yes	        YYYY-MM-DD	    1990-01-15
C	      NIC Number	    Yes	        Text (Unique)	199012345678

Sample Excel File

| Name        | Date of Birth | NIC Number   |
|-------------|---------------|--------------|
| Kalana      | 1990-01-15    | 199012345678 |
| Kamal       | 1985-06-20    | 198556789012 |
| Kasun       | 1992-03-10    | 199234567890 |

Important Notes
Row 1 must be the header row (it will be skipped)
Date format must be YYYY-MM-DD (e.g., 1990-01-15)
NIC numbers must be unique (duplicates are skipped)
Supports files with up to 1,000,000 records
Records are processed in batches of 500 for memory efficiency
File format must be .xlsx or .xls

Running Tests
Run All Tests

cd customer_management_system
mvn test

Test Cases Covered
Test	                         Description
testCreateCustomer	             Creates a customer and verifies
testGetAllCustomers	             Retrieves all customers
testGetCustomerById	             Gets a specific customer by ID
testUpdateCustomer	             Updates customer information
testDeleteCustomer	             Deletes a customer
testDuplicateNicShouldFail	     Rejects duplicate NIC numbers
testCreateCustomerWithMobiles	 Creates customer with mobile numbers
testMissingNameShouldFail	     Validates mandatory fields
testCustomerNotFound	         Handles non-existent customer
testCustomerWithFamilyMembers	 Links family members correctly

Expected Test Result

Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

Database Schema

country
├── id (PK)
└── name (UNIQUE)

city
├── id (PK)
├── name
└── country_id (FK → country.id)

customer
├── id (PK)
├── name
├── date_of_birth
└── nic_number (UNIQUE)

mobile_number
├── id (PK)
├── number
└── customer_id (FK → customer.id)

address
├── id (PK)
├── address_line_1
├── address_line_2
├── city_id (FK → city.id)
├── country_id (FK → country.id)
└── customer_id (FK → customer.id)

customer_family
├── customer_id (FK → customer.id)
└── family_member_id (FK → customer.id)
Troubleshooting

Backend Issues
Problem	                        Solution
Port 8080 already in use	    Kill process: netstat -ano findstr 8080
Database connection failed	    Check MariaDB is running and password is correct
GSS-API authentication error	Add ?restrictedAuth=mysql_native_password to JDBC URL
Tables already exist warning	Set spring.jpa.hibernate.ddl-auto=none

Frontend Issues
Problem	                    Solution
Cannot connect to backend	Make sure Spring Boot is running on port 8080
CORS error	                Check CorsConfig.java allows http://localhost:3000
npm install fails	        Delete node_modules folder and run npm install again
Port 3000 already in use	Run npm start and choose a different port

Database Issues
Problem	                        Solution
Access denied	                Check username and password in application.properties
Database not found	            Run CREATE DATABASE customerdb; in MySQL Workbench
Foreign key constraint error	Run DDL in correct order (country → city → customer)

Performance Considerations
Bulk Import (1,000,000 Records)
Records processed in batches of 500
Duplicate NIC numbers automatically skipped
Memory efficient streaming approach
Progress logged to console
Request timeout set to 5 minutes

Database Optimization
Connection pooling with HikariCP
Batch insert for bulk operations
Indexed NIC number column (UNIQUE)
Lazy loading for relationships

Notes
Cities and Countries are master data - managed only in the database
Family member relationships are bidirectional
All dates use ISO format (YYYY-MM-DD)
API returns proper HTTP status codes
All errors return meaningful messages

Author
Name: Kalana
Project: Customer Management System Assignment
Date: 2026
