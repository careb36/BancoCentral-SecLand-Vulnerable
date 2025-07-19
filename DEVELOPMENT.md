# SecLand Central Bank - Development Guide

![Development](https://img.shields.io/badge/Development-Ready-green?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen?style=flat-square&logo=spring)
![Node.js](https://img.shields.io/badge/Node.js-18+-green?style=flat-square&logo=node.js)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=flat-square&logo=docker)

> ⚠️ **EDUCATIONAL SECURITY WARNING**: This development environment contains intentional security vulnerabilities designed for educational purposes. Use only in controlled, isolated environments.

## 🎯 Development Overview

This guide provides comprehensive instructions for setting up and developing the SecLand Central Bank vulnerable banking application. The application is designed for cybersecurity education and contains 45+ intentional security vulnerabilities.

## 🏗️ Architecture Summary

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend      │    │     Backend      │    │    Database     │
│   (Next.js)     │◄──►│  (Spring Boot)   │◄──►│  (PostgreSQL)   │
│   Port: 3000    │    │   Port: 8080     │    │   Port: 5432    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                        │                        │
         └────────────────────────┼────────────────────────┘
                                  │
                          ┌──────────────┐
                          │    Docker    │
                          │   Network    │
                          └──────────────┘
```

## 📋 Prerequisites

### Required Software

| Tool | Version | Purpose | Download |
|------|---------|---------|----------|
| **Java JDK** | 21+ | Backend development | [OpenJDK](https://openjdk.org/projects/jdk/21/) |
| **Node.js** | 18.0+ | Frontend development | [Node.js](https://nodejs.org/) |
| **PNPM** | 8.0+ | Frontend package management | [PNPM](https://pnpm.io/) |
| **Docker** | 24.0+ | Containerization | [Docker Desktop](https://www.docker.com/products/docker-desktop/) |
| **Git** | 2.40+ | Version control | [Git](https://git-scm.com/) |
| **VS Code** | Latest | IDE (recommended) | [VS Code](https://code.visualstudio.com/) |

### Optional Development Tools

| Tool | Purpose | Installation |
|------|---------|--------------|
| **IntelliJ IDEA** | Alternative Java IDE | [JetBrains](https://www.jetbrains.com/idea/) |
| **Postman** | API testing | [Postman](https://www.postman.com/) |
| **DBeaver** | Database management | [DBeaver](https://dbeaver.io/) |
| **OWASP ZAP** | Security testing | [OWASP ZAP](https://www.zaproxy.org/) |

## 🚀 Quick Start

### 1. Environment Setup

```bash
# Clone the repository
git clone <repository-url>
cd secland-centralbank-vulnerable

# Verify Java installation
java --version  # Should be 21+

# Verify Node.js installation
node --version  # Should be 18+

# Verify Docker installation
docker --version
docker-compose --version
```

### 2. Backend Setup (Spring Boot)

```bash
# Navigate to project root
cd secland-centralbank-vulnerable

# Make Maven wrapper executable (Linux/Mac)
chmod +x mvnw

# Run Maven wrapper (Windows)
# Use mvnw.cmd

# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Start the application in development mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The backend will be available at: `http://localhost:8080`

### 3. Frontend Setup (Next.js)

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies with PNPM
pnpm install

# Start development server
pnpm dev

# Alternative: Start with npm
npm install
npm run dev
```

The frontend will be available at: `http://localhost:3000`

### 4. Database Setup (PostgreSQL)

#### Option A: Docker (Recommended)

```bash
# Start PostgreSQL container
docker-compose up -d db

# Check database connection
docker-compose logs db
```

#### Option B: Local PostgreSQL

```bash
# Install PostgreSQL 15+
# Create database
createdb secland_bank_db

# Configure connection in application-dev.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/secland_bank_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 5. Full Stack Development (Docker)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up -d --build
```

## 🔧 Development Configuration

### VS Code Workspace Setup

1. **Open Workspace**
   ```bash
   code SecLand-Banking.code-workspace
   ```

2. **Install Recommended Extensions**
   - VS Code will prompt to install workspace recommendations
   - Essential extensions are automatically configured

3. **Configure Environment Variables**
   ```bash
   # Create .env file in root directory
   cp .env.example .env
   
   # Edit configuration
   code .env
   ```

### Environment Variables

Create `.env` file in project root:

```bash
# Database Configuration
POSTGRES_DB=secland_bank_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
DATABASE_URL=jdbc:postgresql://localhost:5432/secland_bank_db

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# Frontend Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_BASE_URL=http://localhost:3000

# Security Configuration (Intentionally Weak for Education)
JWT_SECRET=mySecretKey
JWT_EXPIRATION=86400000

# Educational Flags
EDUCATIONAL_MODE=true
SHOW_VULNERABILITIES=true
SECURITY_WARNINGS=true
```

## 📂 Project Structure Deep Dive

### Backend Structure (`src/main/java/com/secland/centralbank/`)

```
com.secland.centralbank/
├── BancoCentralSecLandApplication.java    # Main application entry point
├── config/                                # Configuration classes
│   ├── SecurityConfig.java               # Security configuration (vulnerable)
│   ├── DatabaseConfig.java               # Database setup
│   └── SwaggerConfig.java                # API documentation config
├── controller/                            # REST API controllers
│   ├── AuthController.java               # Authentication endpoints
│   ├── AccountController.java            # Account management
│   ├── TransactionController.java        # Transaction operations
│   └── UserController.java               # User management
├── service/                               # Business logic layer
│   ├── AuthService.java                  # Authentication logic
│   ├── AccountService.java               # Account operations
│   ├── TransactionService.java           # Transaction processing
│   └── UserService.java                  # User management
├── repository/                            # Data access layer
│   ├── UserRepository.java               # User data access
│   ├── AccountRepository.java            # Account data access
│   └── TransactionRepository.java        # Transaction data access
├── model/                                 # Entity classes
│   ├── User.java                         # User entity
│   ├── Account.java                      # Account entity
│   └── Transaction.java                  # Transaction entity
├── dto/                                   # Data transfer objects
│   ├── AuthRequest.java                  # Authentication requests
│   ├── TransferRequest.java              # Transfer requests
│   └── Response.java                     # API responses
├── exception/                             # Custom exceptions
│   ├── GlobalExceptionHandler.java       # Global error handling
│   └── CustomExceptions.java             # Application exceptions
├── filter/                                # Security filters
│   └── JwtAuthFilter.java                # JWT authentication filter
└── util/                                  # Utility classes
    ├── JwtUtil.java                      # JWT token utilities
    └── SecurityUtil.java                 # Security helper methods
```

### Frontend Structure (`frontend/`)

```
frontend/
├── app/                          # Next.js App Router
│   ├── layout.tsx               # Root layout component
│   ├── page.tsx                 # Landing/login page
│   ├── globals.css              # Global styles
│   ├── providers.tsx            # Context providers
│   └── dashboard/               # Dashboard routes
│       ├── page.tsx             # Dashboard main page
│       ├── accounts/            # Account management pages
│       ├── transfers/           # Transfer pages
│       └── transactions/        # Transaction history pages
├── components/                   # Reusable components
│   ├── auth/                    # Authentication components
│   │   ├── login-form.tsx       # Login form (vulnerable)
│   │   └── register-form.tsx    # Registration form
│   ├── dashboard/               # Dashboard components
│   │   ├── accounts-list.tsx    # Account listing
│   │   ├── transfer-form.tsx    # Money transfer form
│   │   └── transaction-history.tsx # Transaction display
│   ├── layout/                  # Layout components
│   │   ├── header.tsx           # Navigation header
│   │   └── footer.tsx           # Page footer
│   └── ui/                      # Base UI components (shadcn/ui)
├── hooks/                       # Custom React hooks
│   ├── use-auth.tsx             # Authentication state
│   ├── use-accounts.tsx         # Account data management
│   └── use-toast.ts             # Notification system
├── lib/                         # Utility libraries
│   ├── api-service.ts           # API client
│   └── utils.ts                 # Helper functions
├── types/                       # TypeScript definitions
│   ├── account.ts               # Account-related types
│   ├── transaction.ts           # Transaction types
│   └── user.ts                  # User types
└── public/                      # Static assets
    ├── placeholder-logo.svg     # Application logo
    └── images/                  # Additional images
```

## 🔨 Development Workflows

### Backend Development

#### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Run tests with coverage
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

#### Building and Packaging

```bash
# Clean build
./mvnw clean compile

# Package application (skip tests for speed)
./mvnw clean package -DskipTests

# Build Docker image
docker build -t secland-backend .

# Run packaged application
java -jar target/centralbank-educational-1.0.0-SNAPSHOT.jar
```

#### Database Development

```bash
# Reset database schema
./mvnw flyway:clean flyway:migrate

# Generate database migration
./mvnw flyway:baseline

# Check migration status
./mvnw flyway:info

# Manually run data scripts
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.sql.init.mode=always"
```

### Frontend Development

#### Development Server

```bash
# Start with hot reload
pnpm dev

# Start with custom port
pnpm dev -- -p 3001

# Start with debug mode
NODE_OPTIONS='--inspect' pnpm dev
```

#### Code Quality and Testing

```bash
# Type checking
pnpm type-check

# Linting
pnpm lint
pnpm lint:fix

# Build production bundle
pnpm build

# Test production build locally
pnpm start

# Analyze bundle size
pnpm analyze
```

#### Component Development

```bash
# Add new UI component (using shadcn/ui)
npx shadcn-ui@latest add button

# Generate component template
mkdir -p components/features/new-feature
touch components/features/new-feature/index.tsx
```

### Full Stack Development

#### Development Tasks (VS Code)

Use VS Code tasks (Ctrl+Shift+P → "Tasks: Run Task"):

- **🔨 Build Backend (Maven)** - Compile Java code
- **🚀 Start Backend (Development Profile)** - Run Spring Boot with dev profile
- **🔨 Build Frontend (Next.js)** - Build React application
- **🚀 Start Frontend (Development Server)** - Start Next.js dev server
- **🐳 Docker: Build and Start All Services** - Full stack Docker deployment

#### Development Debugging

##### Backend Debugging (VS Code)

1. Set breakpoints in Java code
2. Use "🐛 Debug Spring Boot Application" launch configuration
3. Debug port: `5005`

##### Frontend Debugging (VS Code)

1. Set breakpoints in TypeScript/React code
2. Use "🐛 Debug Next.js Application" launch configuration
3. Debug in browser with VS Code integration

##### Database Debugging

```bash
# Connect to PostgreSQL container
docker exec -it secland-db psql -U postgres -d secland_bank_db

# View application logs
docker-compose logs -f backend

# Monitor database queries
docker-compose logs -f db
```

## 🔐 Security Development Notes

### Educational Vulnerabilities Overview

The application contains these intentional security vulnerabilities for educational purposes:

#### Backend Vulnerabilities (25+)

1. **SQL Injection (CWE-89)**
   - Location: `AccountController.java`, `TransactionController.java`
   - Example: Raw SQL queries without parameterization

2. **Authentication Bypass (CWE-287)**
   - Location: `JwtAuthFilter.java`, `SecurityConfig.java`
   - Example: Weak JWT validation logic

3. **Command Injection (CWE-78)**
   - Location: File upload endpoints
   - Example: Unsafe file processing

4. **Insecure Direct Object References (CWE-639)**
   - Location: Account and transaction endpoints
   - Example: Missing authorization checks

5. **Sensitive Data Exposure (CWE-200)**
   - Location: Error handling, logging
   - Example: Stack traces in production

#### Frontend Vulnerabilities (20+)

1. **Cross-Site Scripting (CWE-79)**
   - Location: React components with `dangerouslySetInnerHTML`
   - Example: Unescaped user input

2. **Client-Side Authentication Bypass (CWE-602)**
   - Location: Authentication components
   - Example: Frontend-only route protection

3. **Insecure Storage (CWE-922)**
   - Location: Token storage in localStorage
   - Example: Sensitive data in browser storage

### Security Testing Guidelines

#### Automated Security Testing

```bash
# Backend security scanning
./mvnw dependency-check:check

# Frontend dependency vulnerabilities
pnpm audit

# Container security scanning
docker scan secland-backend:latest
```

#### Manual Security Testing

1. **SQL Injection Testing**
   ```bash
   # Test login endpoint
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin'\''--","password":"any"}'
   ```

2. **XSS Testing**
   ```javascript
   // Test input fields in browser console
   document.getElementById('username').value = '<script>alert("XSS")</script>';
   ```

3. **Authentication Bypass Testing**
   ```bash
   # Test JWT manipulation
   curl -X GET http://localhost:8080/api/accounts \
     -H "Authorization: Bearer invalid.jwt.token"
   ```

## 🐛 Troubleshooting

### Common Development Issues

#### Backend Issues

**Issue**: Application fails to start
```bash
# Check Java version
java --version

# Check port availability
netstat -an | grep 8080

# Check database connection
docker-compose logs db
```

**Issue**: Database connection errors
```bash
# Reset database
docker-compose down -v
docker-compose up -d db

# Check connection string in application-dev.properties
```

**Issue**: Maven build failures
```bash
# Clean and rebuild
./mvnw clean compile

# Update dependencies
./mvnw dependency:resolve

# Skip tests if failing
./mvnw compile -DskipTests
```

#### Frontend Issues

**Issue**: Module resolution errors
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
pnpm install

# Clear Next.js cache
rm -rf .next
pnpm dev
```

**Issue**: TypeScript compilation errors
```bash
# Check TypeScript configuration
pnpm type-check

# Regenerate types
pnpm build
```

**Issue**: API connection problems
```bash
# Check backend is running
curl http://localhost:8080/api/health

# Verify CORS configuration
# Check browser network tab for CORS errors
```

#### Docker Issues

**Issue**: Container build failures
```bash
# Clean Docker cache
docker system prune -af

# Rebuild specific service
docker-compose build --no-cache backend

# Check logs
docker-compose logs -f
```

**Issue**: Port conflicts
```bash
# Check port usage
netstat -an | grep 8080
netstat -an | grep 3000

# Stop conflicting services
docker-compose down
```

### Development Performance Tips

#### Backend Optimization

```bash
# Use development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Enable hot reload
# Add spring-boot-devtools dependency (already included)

# Optimize JVM for development
export MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"
```

#### Frontend Optimization

```bash
# Use incremental builds
pnpm dev --turbo

# Optimize bundle analysis
pnpm analyze

# Use production build for testing
pnpm build && pnpm start
```

#### Database Optimization

```sql
-- Monitor slow queries
EXPLAIN ANALYZE SELECT * FROM accounts WHERE user_id = 1;

-- Check database performance
SELECT * FROM pg_stat_activity;
```

## 📚 Additional Resources

### Documentation Links

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Next.js Documentation](https://nextjs.org/docs)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Compose Reference](https://docs.docker.com/compose/)

### Security Learning Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Common Weakness Enumeration](https://cwe.mitre.org/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [React Security Best Practices](https://react.dev/learn/security)

### Educational Security Tools

| Tool | Purpose | Website |
|------|---------|---------|
| **OWASP ZAP** | Security testing | [zaproxy.org](https://www.zaproxy.org/) |
| **Burp Suite** | Web application testing | [portswigger.net](https://portswigger.net/burp) |
| **Nikto** | Web server scanner | [cirt.net](https://cirt.net/Nikto2) |
| **SQLMap** | SQL injection testing | [sqlmap.org](http://sqlmap.org/) |

## 📞 Development Support

### Getting Help

1. **Check Documentation** - Review relevant sections in this guide
2. **Search Issues** - Look for similar problems in project issues
3. **Check Logs** - Review application and container logs
4. **Educational Forums** - Discuss with cybersecurity community

### Contributing to Development

1. **Follow Coding Standards** - Use provided formatters and linters
2. **Document Vulnerabilities** - Clearly mark and document educational flaws
3. **Test Changes** - Verify both functionality and educational value
4. **Update Documentation** - Keep guides current with changes

---

**Educational Notice**: This development environment is designed for cybersecurity education and contains intentional vulnerabilities. Use responsibly and only in controlled, isolated environments. Never deploy vulnerable code to production systems.
