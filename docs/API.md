# API Documentation

## Overview

The SecLand Banking API provides RESTful endpoints for banking operations, user management, and authentication. This API is built with Spring Boot and follows REST conventions.

**Base URL**: `http://localhost:8080/api`
**Version**: 1.0
**Authentication**: JWT Bearer Token

## Authentication

### POST /api/auth/register

Register a new user account.

**Request Body**:
```json
{
  "username": "string (required, 3-50 chars)",
  "password": "string (required, 6+ chars)",
  "fullName": "string (required, 2-100 chars)"
}
```

**Response** (201 Created):
```json
{
  "id": 1,
  "username": "johndoe",
  "fullName": "John Doe",
  "createdAt": "2025-07-19T10:00:00Z"
}
```

**Possible Errors**:
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Username already exists

---

### POST /api/auth/login

Authenticate user and receive JWT token.

**Request Body**:
```json
{
  "username": "string (required)",
  "password": "string (required)"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "johndoe",
    "fullName": "John Doe"
  }
}
```

**Possible Errors**:
- `401 Unauthorized`: Invalid credentials
- `400 Bad Request`: Missing username or password

---

## Account Management

### GET /api/accounts

Get all accounts for the authenticated user.

**Headers**:
```
Authorization: Bearer <jwt_token>
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "accountNumber": "ACC001",
    "accountType": "CHECKING",
    "balance": 1500.50,
    "currency": "USD",
    "isActive": true,
    "createdAt": "2025-07-19T10:00:00Z"
  }
]
```

---

### POST /api/accounts

Create a new bank account.

**Headers**:
```
Authorization: Bearer <jwt_token>
```

**Request Body**:
```json
{
  "accountType": "CHECKING|SAVINGS",
  "initialDeposit": 100.00
}
```

**Response** (201 Created):
```json
{
  "id": 2,
  "accountNumber": "ACC002",
  "accountType": "SAVINGS",
  "balance": 100.00,
  "currency": "USD",
  "isActive": true,
  "createdAt": "2025-07-19T10:30:00Z"
}
```

---

### GET /api/accounts/{accountId}

Get details of a specific account.

**Headers**:
```
Authorization: Bearer <jwt_token>
```

**Parameters**:
- `accountId` (path): Account ID

**Response** (200 OK):
```json
{
  "id": 1,
  "accountNumber": "ACC001",
  "accountType": "CHECKING",
  "balance": 1500.50,
  "currency": "USD",
  "isActive": true,
  "createdAt": "2025-07-19T10:00:00Z",
  "lastTransactionAt": "2025-07-19T15:30:00Z"
}
```

---

## Transactions

### GET /api/transactions

Get transaction history for all user accounts.

**Headers**:
```
Authorization: Bearer <jwt_token>
```

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `accountId` (optional): Filter by account ID

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "type": "TRANSFER",
      "amount": 250.00,
      "description": "Transfer to savings",
      "fromAccountId": 1,
      "toAccountId": 2,
      "createdAt": "2025-07-19T15:30:00Z",
      "status": "COMPLETED"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

---

### POST /api/transactions/transfer

Transfer money between accounts.

**Headers**:
```
Authorization: Bearer <jwt_token>
```

**Request Body**:
```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 250.00,
  "description": "Monthly savings transfer"
}
```

**Response** (201 Created):
```json
{
  "id": 2,
  "type": "TRANSFER",
  "amount": 250.00,
  "description": "Monthly savings transfer",
  "fromAccountId": 1,
  "toAccountId": 2,
  "createdAt": "2025-07-19T16:00:00Z",
  "status": "COMPLETED"
}
```

**Possible Errors**:
- `400 Bad Request`: Insufficient funds or invalid account
- `403 Forbidden`: Account doesn't belong to user
- `404 Not Found`: Account not found

---

### POST /api/transactions/deposit

Deposit money into an account.

**Headers**:
```
Authorization: Bearer <jwt_token>
```

**Request Body**:
```json
{
  "accountId": 1,
  "amount": 500.00,
  "description": "Salary deposit"
}
```

**Response** (201 Created):
```json
{
  "id": 3,
  "type": "DEPOSIT",
  "amount": 500.00,
  "description": "Salary deposit",
  "toAccountId": 1,
  "createdAt": "2025-07-19T16:15:00Z",
  "status": "COMPLETED"
}
```

---

## User Profile

### GET /api/users/profile

Get current user profile information.

**Headers**:
```
Authorization: Bearer <jwt_token>
```

**Response** (200 OK):
```json
{
  "id": 1,
  "username": "johndoe",
  "fullName": "John Doe",
  "createdAt": "2025-07-19T10:00:00Z",
  "lastLoginAt": "2025-07-19T14:30:00Z",
  "accountCount": 2,
  "totalBalance": 1750.50
}
```

---

## Health Check

### GET /actuator/health

Check application health status.

**Response** (200 OK):
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

---

## Error Handling

### Standard Error Response

All API errors follow this format:

```json
{
  "timestamp": "2025-07-19T16:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'amount': must be greater than 0",
  "path": "/api/transactions/transfer"
}
```

### HTTP Status Codes

- `200 OK`: Successful GET request
- `201 Created`: Successful POST request
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Missing or invalid token
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server error

---

## Rate Limiting

API endpoints are rate-limited to prevent abuse:

- **Authentication endpoints**: 5 requests per minute per IP
- **Transaction endpoints**: 30 requests per minute per user
- **General endpoints**: 100 requests per minute per user

Rate limit headers:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 99
X-RateLimit-Reset: 1642684800
```

---

## Testing

### Postman Collection

A Postman collection is available for API testing:

```bash
# Import collection
curl -o SecLand-API.postman_collection.json \
  https://raw.githubusercontent.com/careb36/CentralBank-SecLand-Vulnerable/main/docs/postman/SecLand-API.postman_collection.json
```

### Test Users

For development and testing:

| Username | Password | Full Name | Account Types |
|----------|----------|-----------|---------------|
| testuser | password | Demo Test User | Checking, Savings |
| admin | admin123 | System Administrator | Checking |
| carolina_p | carolina123 | Carolina Pereira | Checking, Savings |
| test_user | test123 | Test User | Checking |

---

## Security Notes

⚠️ **IMPORTANT**: This API contains intentional security vulnerabilities for educational purposes:

1. **SQL Injection**: Some endpoints are vulnerable to SQL injection
2. **XSS**: Input validation is deliberately insufficient
3. **IDOR**: Authorization checks may be missing
4. **Sensitive Data Exposure**: Passwords and tokens may be logged
5. **Broken Authentication**: Session management has flaws

**DO NOT USE IN PRODUCTION**

---

For implementation details, see the [Development Guide](./DEVELOPMENT.md).
