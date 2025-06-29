# BancoCentral-SecLand-Vulnerable – Ethical Hacking Lab

This repository contains the source code for **"Banco Central de SecLand"**, a deliberately vulnerable web banking application built with Spring Boot (Java) and PostgreSQL. The main goal of this project is to serve as a **laboratory for ethical hacking research and practice**, as well as to develop an anomaly detection module based on Artificial Intelligence (AI), as part of a Master’s Thesis (TFM).

> **Warning:** This application is deliberately vulnerable and is intended **for educational and research purposes only**. **Do not use in production or with real data.**

## Table of Contents
1. [Project Objective](#project-objective)
2. [Implemented Features](#implemented-features)
3. [Vulnerabilities and Security Practices](#vulnerabilities-and-security-practices)
4. [Technologies Used](#technologies-used)
5. [How to Get Started](#how-to-get-started)
6. [API Endpoints](#api-endpoints)
7. [Sample Data](#sample-data)
8. [License](#license)

## Project Objective

This project aims to provide a controlled environment to:
* Conduct penetration testing (pentesting) on common banking functionalities using **Kali Linux**.
* Study and exploit security vulnerabilities intentionally introduced in both code and business logic.
* Collect detailed logs for the training and validation of an AI-based anomaly detection model.
* Serve as an original research platform for a Master’s Thesis, ensuring no public “solutions” exist for its vulnerabilities.

## Implemented Features

* **User Management:** Customer registration and authentication.
* **Account Management:** Creation of Savings and Checking accounts.
* **Transactions:** Funds transfer between accounts.
* **RESTful API:** All functionality is exposed through a REST API.
* **Mixed Security:** Combines robust security practices with deliberately introduced vulnerabilities.

## Vulnerabilities and Security Practices

This application is designed with a mixed security posture for educational purposes.

### Intentional Vulnerabilities

* **A05:2021-Broken Access Control (IDOR):** The transfer endpoint (`/api/accounts/transfer`) is vulnerable to **Insecure Direct Object Reference (IDOR)**. It does not check that the authenticated user owns the `sourceAccountId`, allowing an attacker to transfer funds from any account just by knowing its ID.
* **Business Logic Flaw:** The transfer functionality does not validate if the source account has sufficient funds, allowing accounts to have a negative balance.

### Secure Practices Implemented (Counterexamples)

* **A02:2021-Cryptographic Failures:** To prevent credential exposure, the system **does not store passwords in plain text**. All passwords are hashed using the **BCrypt** algorithm before being saved to the database, following industry best practices.

## Technologies Used

* **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA
* **Database:** PostgreSQL 15
* **Build Tool:** Maven
* **Containerization:** Docker, Docker Compose
* **Testing:** JUnit 5, Postman
* **Attack Platform:** Kali Linux

## How to Get Started

The project is fully containerized for easy and fast deployment.

1.  **Prerequisites:**
    * [Docker Desktop](https://www.docker.com/products/docker-desktop) installed and running.
    * A Git client.

2.  **Clone and Run:**
    ```bash
    # Clone this repository
    git clone https://github.com/careb36/BancoCentral-SecLand-Vulnerable.git

    # Enter the project directory
    cd BancoCentral-SecLand-Vulnerable

    # Launch the application and database using Docker Compose
    # The --build flag ensures the latest version of the code is compiled
    docker-compose up --build
    ```
    The application will be available at `http://localhost:8080`.

## API Endpoints

### Authentication (`/api/auth`)

* **Register a new user**
    * **Endpoint:** `POST /api/auth/register`
    * **Body:**
        ```json
        {
            "username": "new_user",
            "password": "a_secure_password",
            "fullName": "Full Name"
        }
        ```

* **Log in**
    * **Endpoint:** `POST /api/auth/login`
    * **Body:**
        ```json
        {
            "username": "existing_user",
            "password": "your_password"
        }
        ```

### Accounts (`/api/accounts`)

* **Make a transfer**
    * **Endpoint:** `POST /api/accounts/transfer`
    * **Body:**
        ```json
        {
            "sourceAccountId": 101,
            "destinationAccountId": 201,
            "amount": 500.00,
            "description": "Test transfer"
        }
        ```

## Sample Data

The database is initialized with the following users and accounts for testing:

| Entity  | ID  | Details                                                    |
| :------ | :-: | :-------------------------------------------------------- |
| User    | 1   | `username`: **carolina_p**, `password`: **password123**   |
| User    | 2   | `username`: **test_user**, `password`: **testpass**       |
| Account | 101 | Type: Savings, Balance: 5000.75, Owner: `carolina_p`      |
| Account | 102 | Type: Checking, Balance: 1250.00, Owner: `carolina_p`     |
| Account | 201 | Type: Savings, Balance: 800.50, Owner: `test_user`        |

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

---

> **Warning!** This application is deliberately vulnerable and is designed solely for educational purposes. Do **not** use it in production or with real data.
