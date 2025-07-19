# 🔐 Test Credentials

## Default Users

### Test User (Standard Customer)
- **Username:** `testuser`
- **Password:** `password`
- **Full Name:** Demo Test User
- **Accounts:** 2 (Checking: $2,500.00, Savings: $1,750.25)

### Administrator
- **Username:** `admin`
- **Password:** `password`
- **Full Name:** System Administrator
- **Accounts:** 2 (Checking: $10,000.00, Savings: $25,000.50)

### Carolina (Customer)
- **Username:** `carolina_p`
- **Password:** `password`
- **Full Name:** Carolina Pereira
- **Accounts:** 2 (Savings: $5,000.75, Checking: $1,250.00)

### Test User 2 (Customer)
- **Username:** `test_user`
- **Password:** `password`
- **Full Name:** Test User
- **Accounts:** 1 (Savings: $800.50)

## Quick Login Testing

### Frontend Login
1. Navigate to http://localhost:3000
2. Use any of the credentials above
3. Example: `testuser` / `password`

### API Testing
```bash
# Login via API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password"}'

# Use the returned JWT token for subsequent requests
curl -X GET http://localhost:8080/api/accounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Security Notes

⚠️ **All users share the same weak password `password` for educational purposes**

This demonstrates:
- Weak password policies
- Password reuse across accounts
- Lack of password complexity requirements
- No account lockout mechanisms
