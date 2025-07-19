# SecLand Central Bank

This repository contains the source code for **"SecLand Central Bank"**, a **deliberately vulnerable** web banking application built with Spring Boot (Java) and PostgreSQL. The main objective of this project is to serve as a **laboratory for ethical hacking research and practice**, as well as to develop an Artificial Intelligence (AI) based anomaly detection module as part of a Master's Thesis (TFM).

> **⚠️ CRITICAL WARNING:** This application contains **intentional vulnerabilities** and is designed **ONLY for educational and research purposes**. **DO NOT use in production or with real data.**

## 📋 Table of Contents

1. [Project Purpose](#-project-purpose)
2. [Implemented Vulnerabilities](#-implemented-vulnerabilities)
3. [Implemented Features](#-implemented-features)
4. [Technologies Used](#️-technologies-used)
5. [Quick Setup](#-quick-setup)
6. [API Endpoints](#-api-endpoints)
7. [Test Data](#-test-data)
8. [Vulnerability Documentation](#-vulnerability-documentation)
9. [Educational Use Cases](#-educational-use-cases)
10. [Ethical Considerations](#️-ethical-considerations)
11. [License](#-license)

## 🎯 Project Purpose

This project aims to provide a controlled environment for:

- **Performing penetration testing (pentesting)** on common banking functionalities using **Kali Linux**.
- **Studying and exploiting security vulnerabilities** intentionally introduced in both code and business logic.
- **Collecting detailed logs** for training and validation of an AI-based anomaly detection model.
- **Serving as an original research platform** for a Master's Thesis, ensuring no public "solutions" exist for its vulnerabilities.
- **Educating about security best practices** through comparison of vulnerable vs secure implementations.

## 🔍 Implemented Vulnerabilities

### **Main Vulnerabilities (OWASP Top 10 2021):**

#### **A05:2021 - Broken Access Control (IDOR)**

- ✅ **Account creation for any user** - `POST /api/accounts/create`
- ✅ **Access to other users' accounts** - `GET /api/accounts/user/{username}`
- ✅ **Transfers from any account** - `POST /api/accounts/transfer`
- ✅ **Deposits to any account** - `POST /api/accounts/{accountId}/deposit`
- ✅ **Deletion of any account** - `DELETE /api/accounts/{accountId}`
- ✅ **Access to transaction history** - `GET /api/accounts/{accountId}/transactions`

#### **A03:2021 - Injection**

- ✅ **SQL Injection in search** - `GET /api/transactions/search?description={query}`
- ✅ **XSS in transaction descriptions** - Storage of malicious scripts

#### **A07:2021 - Identification and Authentication Failures**

- ✅ **Weak passwords** - No complexity requirements
- ✅ **No rate limiting** - Unrestricted brute force attacks
- ✅ **No account lockout** - Unlimited login attempts

#### **A01:2021 - Broken Access Control (Business Logic)**

- ✅ **Negative funds** - Transfers resulting in negative balances
- ✅ **Unlimited amounts** - No transaction limit validation

### **Additional Vulnerabilities:**

- ✅ **Lack of input sanitization** - Allows code injection
- ✅ **Insufficient logging** - Lack of security auditing
- ✅ **Informative error handling** - Exposure of sensitive information

## 🏦 Implemented Features

- **User Management:** Customer registration and authentication.
- **Account Management:** Creation of Savings and Checking accounts.
- **Transactions:** Fund transfers between accounts.
- **RESTful API:** All functionality exposed through a REST API.
- **Mixed Security:** Combines robust security practices with intentionally introduced vulnerabilities.
- **Detailed Logging:** Event logging for anomaly analysis.

## 🛠️ Technologies Used

- **Frontend:** Next.js 15.2.4, React 19, TypeScript, Tailwind CSS, Radix UI
- **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA
- **Database:** PostgreSQL 15
- **Build Tool:** Maven (Backend), pnpm (Frontend)
- **Containerization:** Docker, Docker Compose
- **Testing:** JUnit 5, Postman
- **Attack Platform:** Kali Linux
- **Pentesting Tools:** Burp Suite, OWASP ZAP, SQLMap

## 🚀 Quick Setup

The project is fully containerized for easy and quick deployment.

### **Prerequisites:**

- [Docker Desktop](https://www.docker.com/products/docker-desktop) (version 24.0.0+)
- [Git](https://git-scm.com/) (version 2.40.0+)
- [Java Development Kit (JDK)](https://adoptium.net/) (version 21+) - for local development
- [Maven](https://maven.apache.org/) (version 3.9.0+) - for local development
- [Node.js](https://nodejs.org/) (version 18.0.0+) - for frontend development

### **Environment Setup:**

1. **Clone the Repository:**
```bash
git clone https://github.com/careb36/BancoCentral-SecLand-Vulnerable.git
cd BancoCentral-SecLand-Vulnerable
```

2. **Configure Environment Variables:**
Create a `.env` file in the root directory:
```env
# Database Configuration
POSTGRES_DB=secland_bank_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# Application Ports
BACKEND_PORT=8080
FRONTEND_PORT=3000
DB_PORT=5432

# Frontend Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

3. **Start the Application:**

Using Docker (recommended for testing):
```bash
# Build and start all containers
docker-compose up --build

# Start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all containers
docker-compose down
```

For Local Development:
```bash
# Backend (Terminal 1)
# Use Maven Wrapper for consistent build
./mvnw clean install     # Unix/macOS
mvnw.cmd clean install   # Windows PowerShell
./mvnw spring-boot:run   # Unix/macOS
mvnw.cmd spring-boot:run # Windows PowerShell

# Frontend (Terminal 2)
cd frontend
npm install  # or: pnpm install
npm run dev  # or: pnpm dev
```

### **Access Points:**

- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Actuator:** http://localhost:8080/actuator

### **Health Checks:**

```bash
# Check backend health
curl http://localhost:8080/actuator/health

# Check database connection
curl http://localhost:8080/actuator/health/db

# Check frontend
curl http://localhost:3000
```

### **Troubleshooting:**

1. **Port Conflicts:**
```bash
# Find and kill processes using ports
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

2. **Database Issues:**
```bash
# Reset the database
docker-compose down -v
docker-compose up --build
```

3. **Clean Build:**
```bash
# Clean Maven build
mvn clean install -U

# Clean Docker build
docker-compose down --rmi all
docker-compose up --build
```

## 🔌 API Endpoints

### **Authentication (`/api/auth`)**

#### **Register a new user**

**Endpoint:** `POST /api/auth/register`

**Body:**
```json
{
    "username": "new_user",
    "password": "a_secure_password",
    "fullName": "Full Name"
}
```

#### **Login**

**Endpoint:** `POST /api/auth/login`

**Body:**
```json
{
    "username": "existing_user",
    "password": "your_password"
}
```

### **Accounts (`/api/accounts`)**

#### **Make a transfer**

**Endpoint:** `POST /api/accounts/transfer`

**Body:**
```json
{
    "fromAccountId": 101,
    "toAccountNumber": "SEC1-87654321",
    "amount": 500.00,
    "description": "Test transfer"
}
```

#### **Create an account**

**Endpoint:** `POST /api/accounts/create`

**Body:**
```json
{
    "username": "target_user",
    "accountType": "Savings",
    "initialDeposit": 1000.00
}
```

#### **Deposit money**

**Endpoint:** `POST /api/accounts/{accountId}/deposit`

**Body:**
```json
{
    "amount": 250.00
}
```

## 📊 Test Data

The database is initialized with the following users and accounts for testing:

| Entity | ID | Details |
|--------|----|---------------------------------------------------------|
| User   | 1  | `username`: **carolina_p**, `password`: **password123** |
| User   | 2  | `username`: **test_user**, `password`: **testpass**     |
| Account| 101| Type: Savings, Balance: 5000.75, Owner: `carolina_p`   |
| Account| 102| Type: Checking, Balance: 1250.00, Owner: `carolina_p`  |
| Account| 201| Type: Savings, Balance: 800.50, Owner: `test_user`     |

## 📚 Vulnerability Documentation

For a complete guide on implemented vulnerabilities, practice scenarios, and exploitation techniques, see:

**[📖 VULNERABILITIES.md](VULNERABILITIES.md)**

This document includes:
- ✅ Detailed description of each vulnerability
- ✅ Exploitation examples with curl commands
- ✅ Step-by-step practice scenarios
- ✅ Comparison with secure implementations
- ✅ Recommended pentesting tools
- ✅ Ethical considerations and responsible use

## 🎓 Educational Use Cases

### **For Security Students:**

- Learn exploitation techniques in a safe environment
- Practice vulnerability reporting
- Understand the impact of security flaws

### **For Researchers:**

- Develop anomaly detection tools
- Study attack patterns
- Validate mitigation techniques

### **For Professors:**

- Demonstrate security concepts in class
- Assign practical pentesting exercises
- Evaluate security analysis skills

## ⚖️ Ethical Considerations

### **Acceptable Use:**

- ✅ Security learning and education
- ✅ Academic research
- ✅ Pentesting practice in controlled environments
- ✅ Development of defense tools

### **Unacceptable Use:**

- ❌ Use in production systems
- ❌ Use with real user data
- ❌ Malicious or illegal activities
- ❌ Sharing exploits without educational context

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/careb36/CentralBank-SecLand-Vulnerable/blob/main/LICENCE) file for more details.

---

> **⚠️ FINAL WARNING:** This application contains intentional vulnerabilities and is designed solely for educational purposes. **DO NOT use in production or with real data.**

> **🎯 Objective:** Learn from vulnerabilities to build more secure applications.
