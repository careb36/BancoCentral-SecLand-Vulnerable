# 🏦 SecLand Central Bank - Intentionally Vulnerable Banking Application

<div align="center">

[![Security Assessment](https://img.shields.io/badge/Security-Intentionally%20Vulnerable-red)](docs/SECURITY.md)
[![Build Status](https://img.shields.io/badge/Build-Passing-green)](https://github.com/careb36/CentralBank-SecLand-Vulnerable/actions)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](docker-compose.yaml)
[![License](https://img.shields.io/badge/License-Educational%20Use%20Only-yellow)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange)](pom.xml)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)](pom.xml)
[![Next.js](https://img.shields.io/badge/Next.js-15.2.4-black)](frontend/package.json)

</div>

## 📋 Overview

SecLand Central Bank is a **deliberately vulnerable modern banking application** designed for cybersecurity education, ethical hacking practice, and security awareness training. This full-stack application demonstrates common web application vulnerabilities in a realistic banking context.

### 🎯 Purpose

This project serves as a **comprehensive security training platform** featuring:

- ✅ **45+ intentional vulnerabilities** spanning OWASP Top 10
- ✅ **Modern technology stack** (Spring Boot + Next.js + PostgreSQL)
- ✅ **Realistic banking features** (accounts, transfers, transactions)
- ✅ **Professional development practices** with security anti-patterns
- ✅ **Comprehensive documentation** for learning and assessment
- ✅ **Interactive API documentation** with Swagger/OpenAPI

## 🚀 Quick Start

### Prerequisites

- **Docker & Docker Compose** (recommended)
- **Java 21+** (for local development)
- **Node.js 18+** (for frontend development)
- **Maven 3.9+** (included via wrapper)

### Using Docker (Recommended)

```bash
# Clone and start the application
git clone https://github.com/careb36/CentralBank-SecLand-Vulnerable.git
cd CentralBank-SecLand-Vulnerable

# Build and start all services
docker-compose up -d --build

# Access the application
open http://localhost:3000  # Frontend (Next.js)
open http://localhost:8080  # Backend API (Spring Boot)
open http://localhost:8080/swagger-ui.html  # API Documentation
```

### Manual Setup

Detailed setup instructions available in [Development Guide](docs/DEVELOPMENT.md).

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │
│   Frontend      │────│   Backend       │────│   Database      │
│   (Next.js)     │    │   (Spring Boot) │    │   (PostgreSQL)  │
│   Port 3000     │    │   Port 8080     │    │   Port 5432     │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Backend** | Spring Boot | 3.5.3 | REST API, Business Logic, Security |
| **Frontend** | Next.js | 15.2.4 | User Interface, Client-side Logic |
| **Database** | PostgreSQL | 15 | Data Persistence, Transaction Management |
| **Authentication** | JWT | - | Token-based Authentication |
| **Documentation** | Swagger/OpenAPI | 3.0 | Interactive API Documentation |
| **Containerization** | Docker | - | Application Deployment |

## 🔐 Default Test Credentials

> **⚠️ Educational Passwords Only**: All users share weak passwords for demonstration purposes.

| Username | Password | Role | Accounts |
|----------|----------|------|----------|
| `testuser` | `password` | Customer | Checking ($2,500), Savings ($1,750) |
| `admin` | `password` | Administrator | Checking ($10,000), Savings ($25,000) |
| `carolina_p` | `password` | Customer | Checking ($1,250), Savings ($5,000) |
| `test_user` | `password` | Customer | Savings ($800) |

📖 **Detailed credentials**: [Test Credentials Guide](docs/CREDENTIALS.md)

## 🛡️ Security Features (Intentionally Vulnerable)

This application contains **45+ intentional security vulnerabilities** including:

### 🔴 Authentication & Authorization
- **Weak Password Policy** (CWE-521)
- **JWT Token Exposure** (CWE-522)
- **Session Management Issues** (CWE-384)
- **Privilege Escalation** (CWE-269)

### 🔴 Input Validation & Injection
- **SQL Injection** (CWE-89)
- **Cross-Site Scripting (XSS)** (CWE-79)
- **Path Traversal** (CWE-22)
- **Command Injection** (CWE-78)

### 🔴 Business Logic & Data
- **Insecure Direct Object References (IDOR)** (CWE-639)
- **Race Conditions** (CWE-362)
- **Business Logic Bypass** (CWE-840)
- **Sensitive Data Exposure** (CWE-200)

### 🔴 Configuration & Infrastructure
- **Security Misconfiguration** (CWE-16)
- **Insecure Cryptographic Storage** (CWE-312)
- **Insufficient Logging** (CWE-778)
- **CORS Misconfiguration** (CWE-942)

📖 **Complete vulnerability analysis**: [Security Vulnerabilities](docs/security/VULNERABILITIES.md)

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| [� Documentation Index](docs/README.md) | Complete documentation overview |
| [🚨 Security Vulnerabilities](docs/security/VULNERABILITIES.md) | 45+ vulnerabilities catalog |
| [🧪 Testing Guide](docs/security/TESTING.md) | Exploitation procedures and tools |
| [� API Reference](docs/development/API.md) | Complete REST API documentation |
| [� Docker Guide](docs/deployment/DOCKER.md) | Container deployment guide |
| [� Development Guide](DEVELOPMENT.md) | Local development setup |

## 🎓 Educational Use Cases

### For Students
- Learn about common web application vulnerabilities
- Practice secure coding techniques
- Understand the impact of security flaws
- Explore modern development frameworks

### For Instructors
- Demonstrate real-world vulnerability examples
- Teach defensive programming practices
- Assess student security knowledge
- Provide hands-on cybersecurity training

### For Security Professionals
- Practice penetration testing techniques
- Develop security testing methodologies
- Train junior security analysts
- Research AI-based anomaly detection

## 🔍 Testing & Exploitation

### Recommended Tools
- **OWASP ZAP** - Automated vulnerability scanning
- **Burp Suite** - Manual security testing
- **sqlmap** - SQL injection testing
- **Postman** - API testing and exploration
- **Browser Developer Tools** - Client-side analysis

### Testing Workflow
1. **Reconnaissance**: Explore the application functionality
2. **Automated Scanning**: Use tools like OWASP ZAP
3. **Manual Testing**: Verify and exploit vulnerabilities
4. **Privilege Escalation**: Attempt to gain admin access
5. **Data Extraction**: Test for sensitive data exposure
6. **Documentation**: Record findings and impact

## 🚦 API Endpoints

### Authentication
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register` - User registration

### Account Management
- `GET /api/accounts` - List user accounts
- `POST /api/accounts/create` - Create new account
- `POST /api/accounts/{id}/deposit` - Deposit funds
- `DELETE /api/accounts/{id}` - Delete account

### Transactions
- `POST /api/accounts/transfer` - Transfer money
- `GET /api/accounts/{id}/transactions` - Transaction history

### Administrative (Vulnerable)
- `GET /api/accounts/user/{username}` - Get user accounts (IDOR)

🔗 **Interactive API Documentation**: http://localhost:8080/swagger-ui.html

## 📦 Project Structure

```
CentralBank-SecLand-Vulnerable/
├── 🐋 docker-compose.yaml           # Container orchestration
├── 🐋 Dockerfile                    # Backend container
├── 📄 pom.xml                       # Maven configuration
├── 📄 README.md                     # This file
├── 📁 docs/                         # Documentation
│   ├── 📄 API.md                    # API documentation
│   ├── 📄 CREDENTIALS.md            # Test credentials
│   ├── 📄 DEVELOPMENT.md            # Development guide
│   └── 📄 SECURITY.md               # Security assessment
├── 📁 src/                          # Backend source code
│   ├── 📁 main/java/                # Java source files
│   ├── 📁 main/resources/           # Configuration & SQL
│   └── 📁 test/java/                # Test files
├── 📁 frontend/                     # Frontend application
│   ├── 📁 app/                      # Next.js pages
│   ├── 📁 components/               # React components
│   ├── 📁 lib/                      # Utilities
│   ├── 📁 hooks/                    # Custom hooks
│   └── 📄 package.json              # Frontend dependencies
└── 📁 scripts/                      # Development scripts
```

## 🤝 Contributing

This is an educational project. Contributions that add new vulnerabilities or improve the learning experience are welcome.

### Guidelines
1. **Maintain Educational Value**: All changes should serve educational purposes
2. **Document Vulnerabilities**: New vulnerabilities must be documented
3. **Professional Code Quality**: Follow existing code standards
4. **Test Coverage**: Include appropriate tests
5. **Security Warnings**: Clearly mark vulnerable code sections

## ⚠️ Important Disclaimers

### 🚨 SECURITY WARNING
**This application is intentionally vulnerable and must NEVER be used in production environments.**

### Legal and Ethical Use
- ✅ **Authorized testing environments only**
- ✅ **Educational and research purposes**
- ✅ **Follow responsible disclosure practices**
- ❌ **No unauthorized testing on third-party systems**
- ❌ **No deployment in production environments**
- ❌ **No use for malicious purposes**

### Educational Context
This application is developed for:
- Cybersecurity education and training
- Academic research purposes
- Ethical hacking practice
- Security awareness demonstrations

## 📜 License

This project is licensed under the Educational Use Only License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Next.js team for the modern React framework
- OWASP for security vulnerability classifications
- Security research community for vulnerability patterns

---

<div align="center">
<strong>⚠️ FOR EDUCATIONAL PURPOSES ONLY ⚠️</strong><br>
<em>Never deploy this application in production environments</em>
</div>

### Liability
The authors and contributors are not responsible for any misuse of this application. Users must comply with applicable laws and regulations.

## 🤝 Contributing

Contributions are welcome! Please read our contribution guidelines:

1. Fork the repository
2. Create a feature branch
3. Follow coding standards
4. Add comprehensive tests
5. Update documentation
6. Submit a pull request

## 📞 Support

- 📖 **Documentation**: Check the [docs/](docs/) directory
- 🐛 **Issues**: [GitHub Issues](https://github.com/careb36/CentralBank-SecLand-Vulnerable/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/careb36/CentralBank-SecLand-Vulnerable/discussions)

## 📜 License

This project is licensed for **Educational Use Only**. See [LICENSE](LICENSE) for details.

---

**🎯 Learning Objective**: Understand web application security through hands-on experience with real vulnerabilities in a safe, controlled environment.

**⭐ Star this repository** if you find it useful for your security education journey! 