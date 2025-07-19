# Changelog

All notable changes to the SecLand Banking Application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-07-19

### Added

#### Backend (Spring Boot)
- **Authentication System**: JWT-based authentication with intentional vulnerabilities
- **Banking Core Features**: Account management, transfers, transactions, and deposits
- **Security Vulnerabilities**: 45+ intentional security flaws for educational purposes
  - SQL Injection vulnerabilities in multiple endpoints
  - Authentication bypass mechanisms
  - Cross-Site Scripting (XSS) vectors
  - Insecure Direct Object References (IDOR)
  - Broken session management
  - Command injection vulnerabilities
- **Database Layer**: PostgreSQL integration with JPA/Hibernate
- **REST API**: Comprehensive banking API with detailed documentation
- **Health Checks**: Application monitoring and status endpoints
- **Password Utilities**: BCrypt hash generation for development

#### Frontend (Next.js)
- **Modern UI**: Responsive banking interface with Tailwind CSS
- **User Authentication**: Login and registration with vulnerable token handling
- **Banking Dashboard**: Complete account overview and management
- **Transfer System**: Money transfer between accounts with real-time updates
- **Transaction History**: Comprehensive transaction tracking and filtering
- **Account Management**: Create and manage multiple account types
- **Security Flaws**: Client-side vulnerabilities for educational purposes
  - Client-side token storage in localStorage
  - XSS vulnerabilities in React components
  - Unvalidated redirects and DOM manipulation

#### Infrastructure
- **Docker Support**: Complete containerization with Docker Compose
- **Database Setup**: Automated PostgreSQL setup with sample data
- **Health Monitoring**: Container health checks and monitoring
- **Development Environment**: Hot reload and development server setup

#### Documentation
- **Comprehensive Guides**: Development setup, API documentation, security assessment
- **Security Analysis**: Detailed vulnerability documentation with 45+ findings
- **Professional README**: Clear project overview with professional standards
- **Educational Resources**: Learning objectives and testing guidelines

### Security Vulnerabilities (Intentional)

#### Critical (CVSS 9.0+)
- **SQL Injection** (CWE-89): Database queries vulnerable to injection attacks
- **Authentication Bypass** (CWE-287): JWT validation bypass mechanisms
- **Command Injection** (CWE-78): File upload processing vulnerabilities

#### High (CVSS 7.0-8.9)
- **Cross-Site Scripting** (CWE-79): Multiple XSS vectors in frontend
- **IDOR** (CWE-639): Missing authorization checks on sensitive operations
- **Broken Session Management** (CWE-384): Insecure token and session handling

#### Medium (CVSS 4.0-6.9)
- **CSRF** (CWE-352): Cross-Site Request Forgery vulnerabilities
- **Information Disclosure** (CWE-200): Sensitive data exposure in logs and responses
- **Weak Password Policy** (CWE-521): Insufficient password requirements

#### Low (CVSS 0.1-3.9)
- **Security Misconfiguration** (CWE-16): Overly permissive CORS and security settings
- **Dependency Vulnerabilities**: Known CVEs in third-party components

### Technical Stack

#### Backend
- **Java**: OpenJDK 21
- **Spring Boot**: 3.x with Spring Security
- **Database**: PostgreSQL 15 with JPA/Hibernate
- **Build Tool**: Maven 3.9+ with wrapper
- **Container**: Eclipse Temurin base image

#### Frontend
- **Next.js**: 15.2.4 with App Router
- **React**: 19.x with modern hooks
- **TypeScript**: Latest with strict configuration
- **Styling**: Tailwind CSS 3.x
- **Package Manager**: PNPM for dependency management
- **Container**: Node.js 18 Alpine

#### Infrastructure
- **Containerization**: Docker with multi-stage builds
- **Orchestration**: Docker Compose with health checks
- **Database**: PostgreSQL 15 Alpine
- **Monitoring**: Application and container health monitoring

### Development Features

#### Code Quality
- **Linting**: ESLint for TypeScript/JavaScript
- **Formatting**: Prettier for consistent code style
- **Type Safety**: Strict TypeScript configuration
- **Testing**: Jest and Spring Boot Test framework

#### Development Tools
- **Hot Reload**: Development server with automatic reloading
- **API Documentation**: Comprehensive REST API documentation
- **Database Tools**: Development data seeding and utilities
- **IDE Support**: Configuration for VS Code and IntelliJ IDEA

### Educational Value

#### Learning Objectives
- Understanding common web application vulnerabilities
- Hands-on experience with OWASP Top 10 vulnerabilities
- Secure coding practices demonstration (by showing what not to do)
- Security testing and assessment methodologies
- Incident response and vulnerability analysis

#### Target Audience
- **Students**: Learning cybersecurity fundamentals
- **Instructors**: Teaching web application security
- **Security Professionals**: Training and skills development
- **Researchers**: Academic security research

### Deployment

#### Supported Environments
- **Development**: Local development with hot reload
- **Docker**: Complete containerized deployment
- **Testing**: Isolated testing environments only

#### Requirements
- **Java**: OpenJDK 21+
- **Node.js**: 18.x+
- **Docker**: 24.x+ with Docker Compose 2.x+
- **Memory**: Minimum 4GB RAM recommended
- **Storage**: 2GB free space for containers and data

### Known Issues

#### By Design (Educational)
- Multiple critical security vulnerabilities (intentional)
- Weak authentication and authorization mechanisms
- Insufficient input validation and sanitization
- Insecure data storage and transmission

#### Limitations
- Not suitable for production use
- Limited scalability (educational focus)
- No real banking integrations
- Simplified business logic for learning purposes

---

## Release Notes

### Version 1.0.0 - Initial Educational Release

This initial release provides a complete, intentionally vulnerable banking application designed for cybersecurity education. The application includes 45+ documented security vulnerabilities spanning the OWASP Top 10 and beyond.

**Key Features:**
- Complete modern banking interface
- Comprehensive vulnerability suite
- Professional documentation
- Docker-based deployment
- Educational resources and guides

**Security Focus:**
This release emphasizes educational value through real-world vulnerability examples while maintaining professional development standards in non-security aspects.

**Target Use:**
Designed for controlled educational environments with proper supervision and security measures.

---

## Future Roadmap

### Planned Enhancements
- Additional vulnerability categories
- Enhanced documentation and tutorials
- Extended test coverage
- Performance optimization
- Additional deployment options

### Educational Improvements
- Interactive vulnerability tutorials
- Automated security scanning demos
- Extended API examples
- Video documentation
- Assessment tools

---

**Note**: This changelog documents an intentionally vulnerable application designed for educational purposes only. All security vulnerabilities are documented and intended for learning cybersecurity concepts in controlled environments.
