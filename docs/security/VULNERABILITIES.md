# 🚨 SECURITY VULNERABILITIES DOCUMENTATION

## ⚠️ WARNING
This application contains **INTENTIONAL SECURITY VULNERABILITIES** for educational and ethical hacking purposes. **NEVER use this code in production environments!**

---

## 🎯 VULNERABILITY CATALOG

### 1. 🔓 IDOR (Insecure Direct Object Reference) - **CRITICAL**

#### **Location:** `/api/accounts/transfer` endpoint
#### **Affected Files:**
- `AccountController.java`
- `TransactionServiceImpl.java`

#### **Description:**
The money transfer endpoint does not verify that the authenticated user owns the source account. Any authenticated user can transfer money from any account by simply knowing the account ID.

#### **Exploitation Steps:**
1. Login with any valid account
2. Intercept the transfer request
3. Change the `fromAccountId` to any other account ID
4. The transfer will succeed even if you don't own the source account

#### **Example Exploit:**
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "fromAccountId": 1,
    "toAccountNumber": "YOUR-ACCOUNT",
    "amount": 1000000,
    "description": "Unauthorized transfer"
  }'
```

#### **Impact:** 
- **Financial theft**
- **Unauthorized fund transfers**
- **Complete compromise of banking system integrity**

---

### 2. 💰 Business Logic Flaw - **CRITICAL**

#### **Location:** `TransactionServiceImpl.performTransfer()`
#### **Affected Files:**
- `TransactionServiceImpl.java`

#### **Description:**
The transfer function does not validate if the source account has sufficient funds before processing the transfer, allowing accounts to have negative balances.

#### **Exploitation Steps:**
1. Find any account with a small balance
2. Transfer an amount larger than the balance
3. The account will have a negative balance

#### **Example:**
- Account balance: $100
- Transfer amount: $50,000
- Result: Account balance becomes -$49,900

#### **Impact:**
- **Unlimited money creation**
- **Banking system insolvency**
- **Regulatory compliance violations**

---

### 3. 🗃️ Information Disclosure - **HIGH**

#### **Location:** `/api/accounts` endpoint
#### **Affected Files:**
- `AccountController.java`
- `AccountServiceImpl.java`

#### **Description:**
The accounts endpoint returns ALL accounts in the system to any authenticated user, exposing sensitive financial information.

#### **Exploitation Steps:**
1. Login with any account
2. Call `GET /api/accounts`
3. Receive complete list of all user accounts and balances

#### **Example Exploit:**
```bash
curl -X GET http://localhost:8080/api/accounts \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### **Impact:**
- **Privacy violations**
- **Sensitive financial data exposure**
- **Account enumeration**

---

### 4. 🔍 IDOR in Transaction History - **HIGH**

#### **Location:** `/api/accounts/{accountId}/transactions`
#### **Affected Files:**
- `AccountController.java`
- `TransactionServiceImpl.java`

#### **Description:**
Any authenticated user can view transaction history for any account by providing the account ID.

#### **Exploitation Steps:**
1. Login with any account
2. Try different account IDs in the URL: `/api/accounts/1/transactions`
3. View sensitive transaction data for other users

#### **Impact:**
- **Financial privacy breach**
- **Transaction pattern analysis by attackers**
- **Regulatory compliance violations**

---

### 5. 💉 SQL Injection - **HIGH**

#### **Location:** `TransactionServiceImpl.searchTransactionsByDescription()`
#### **Affected Files:**
- `TransactionServiceImpl.java`

#### **Description:**
The transaction search function constructs SQL queries using string concatenation without parameterization.

#### **Exploitation Steps:**
1. Call the search endpoint with malicious input
2. Inject SQL commands through the description parameter

#### **Example Exploit:**
```sql
'; DROP TABLE transactions; --
```

#### **Impact:**
- **Database compromise**
- **Data loss**
- **Unauthorized data access**

---

### 6. 🕸️ Stored XSS (Cross-Site Scripting) - **MEDIUM**

#### **Location:** Transaction descriptions
#### **Affected Files:**
- `TransactionServiceImpl.java`
- Frontend JavaScript

#### **Description:**
Transaction descriptions are stored and returned without sanitization, allowing malicious scripts to be executed in users' browsers.

#### **Exploitation Steps:**
1. Create a transfer with malicious JavaScript in the description
2. When other users view transaction history, the script executes

#### **Example Exploit:**
```javascript
<script>alert('XSS Attack! Cookie: ' + document.cookie)</script>
```

#### **Impact:**
- **Session hijacking**
- **User account compromise**
- **Malicious actions on behalf of users**

---

### 7. 📁 Path Traversal - **HIGH**

#### **Location:** `/api/files/download` and `/api/files/read/` endpoints
#### **Affected Files:**
- `FileController.java`

#### **Description:**
File download and read endpoints accept arbitrary file paths without validation, allowing access to files outside the intended directory.

#### **Exploitation Steps:**
1. Use path traversal sequences in file parameters
2. Access sensitive system files

#### **Example Exploits:**
```bash
# Read application properties
GET /api/files/read/../../../application.properties

# Download system files
GET /api/files/download?filename=../../../etc/passwd

# List directories
GET /api/files/list?directory=../../../
```

#### **Impact:**
- **Sensitive file disclosure**
- **System information leakage**
- **Configuration exposure**

---

### 8. ☠️ XXE (XML External Entity) - **HIGH**

#### **Location:** `/api/xml/import`, `/api/xml/validate`, `/api/xml/process` endpoints
#### **Affected Files:**
- `XmlController.java`

#### **Description:**
XML processing endpoints do not disable external entity processing, allowing XXE attacks.

#### **Exploitation Steps:**
1. Send malicious XML with external entity references
2. Access local files or perform SSRF attacks

#### **Example Exploit:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE transaction [
  <!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<transaction>
  <description>&xxe;</description>
</transaction>
```

#### **Impact:**
- **Local file access**
- **Server-Side Request Forgery (SSRF)**
- **Denial of Service (DoS)**

---

### 9. 🏗️ Missing Authorization Context - **MEDIUM**

#### **Location:** Multiple endpoints
#### **Affected Files:**
- All controller classes

#### **Description:**
Most endpoints do not properly extract and validate user identity from the authentication context.

#### **Impact:**
- **Authorization bypass**
- **Privilege escalation**
- **Unauthorized operations**

---

### 10. 📊 Information Leakage through Error Messages - **LOW**

#### **Location:** Various exception handlers
#### **Affected Files:**
- `XmlController.java`
- `FileController.java`

#### **Description:**
Detailed error messages expose internal system information and file paths.

#### **Impact:**
- **System reconnaissance**
- **Internal structure disclosure**
- **Attack surface mapping**

---

## 🛡️ SECURE IMPLEMENTATION GUIDELINES

### For Educational Reference:

#### **How to Fix IDOR:**
```java
@PreAuthorize("@accountService.isAccountOwner(authentication.name, #accountId)")
public ResponseEntity<?> getAccount(@PathVariable Long accountId) {
    // Implementation
}
```

#### **How to Fix SQL Injection:**
```java
@Query("SELECT t FROM Transaction t WHERE t.description LIKE %:description%")
List<Transaction> findByDescriptionContaining(@Param("description") String description);
```

#### **How to Fix XXE:**
```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
```

#### **How to Fix Path Traversal:**
```java
Path safePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
if (!safePath.startsWith(Paths.get(UPLOAD_DIR))) {
    throw new SecurityException("Path traversal attempt detected");
}
```

---

## 🔬 TESTING METHODOLOGY

### **Recommended Testing Tools:**
- **Burp Suite Professional** - For web application security testing
- **OWASP ZAP** - For automated vulnerability scanning
- **SQLMap** - For SQL injection testing
- **XXEinjector** - For XXE vulnerability testing
- **Postman** - For API endpoint testing

### **Testing Checklist:**
- [ ] Test all IDOR vulnerabilities
- [ ] Verify business logic flaws
- [ ] Test SQL injection points
- [ ] Verify XSS vulnerabilities
- [ ] Test path traversal attacks
- [ ] Test XXE vulnerabilities
- [ ] Verify information disclosure
- [ ] Test authorization bypasses

---

## 📚 EDUCATIONAL RESOURCES

- **OWASP Top 10**: https://owasp.org/www-project-top-ten/
- **OWASP Testing Guide**: https://owasp.org/www-project-web-security-testing-guide/
- **PortSwigger Web Security Academy**: https://portswigger.net/web-security
- **NIST Cybersecurity Framework**: https://www.nist.gov/cyberframework

---

**Remember: This application is for educational purposes only. Use responsibly and ethically!**
