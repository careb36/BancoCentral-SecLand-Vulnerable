# SecLand Central Bank - Frontend Application

![Next.js](https://img.shields.io/badge/Next.js-15.2.4-black?style=flat-square&logo=next.js)
![React](https://img.shields.io/badge/React-19-blue?style=flat-square&logo=react)
![TypeScript](https://img.shields.io/badge/TypeScript-5+-blue?style=flat-square&logo=typescript)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3+-blue?style=flat-square&logo=tailwindcss)

> ⚠️ **EDUCATIONAL SECURITY WARNING**: This frontend application contains **20+ intentional security vulnerabilities** designed for educational purposes. **NEVER deploy in production environments.**

## 🎯 Educational Purpose

This is the frontend component of the SecLand Central Bank vulnerable banking application, designed specifically for cybersecurity education, training, and research. The application demonstrates real-world frontend security vulnerabilities in a controlled environment.

## 🏗️ Architecture Overview

Modern React-based banking interface built with Next.js 15.2.4, featuring:

- **Server-Side Rendering (SSR)** with Next.js App Router
- **Type-Safe Development** with TypeScript 5+
- **Modern UI Components** with Radix UI and Tailwind CSS
- **Professional Component Architecture** with separation of concerns
- **Intentional Security Vulnerabilities** for educational analysis

## 📁 Project Structure

```
frontend/
├── app/                    # Next.js App Router structure
│   ├── globals.css        # Global styles and CSS variables
│   ├── layout.tsx         # Root layout with providers
│   ├── page.tsx           # Landing/login page
│   ├── providers.tsx      # Context providers setup
│   └── api/               # API route handlers
├── components/            # Reusable UI components
│   ├── auth/              # Authentication components
│   │   ├── login-form.tsx
│   │   └── register-form.tsx
│   ├── dashboard/         # Banking dashboard components
│   │   ├── accounts-list.tsx
│   │   ├── dashboard.tsx
│   │   ├── transaction-history.tsx
│   │   └── transfer-form.tsx
│   ├── layout/            # Layout components
│   │   ├── header.tsx
│   │   └── footer.tsx
│   └── ui/                # Base UI components (shadcn/ui)
├── hooks/                 # Custom React hooks
│   ├── use-auth.tsx       # Authentication state management
│   ├── use-accounts.tsx   # Account data management
│   └── use-toast.ts       # Toast notification system
├── lib/                   # Utility libraries
│   ├── api-service.ts     # API client and HTTP services
│   └── utils.ts           # Utility functions and helpers
├── types/                 # TypeScript type definitions
│   ├── account.ts         # Account-related types
│   └── transaction.ts     # Transaction-related types
└── public/                # Static assets
    ├── placeholder-logo.svg
    └── placeholder-user.jpg
```

## 🛡️ Intentional Security Vulnerabilities

This frontend contains the following educational security vulnerabilities:

### 🔴 Critical Vulnerabilities
- **Client-Side Token Storage** (CWE-922): JWT tokens stored in localStorage
- **DOM-Based XSS** (CWE-79): Unsafe DOM manipulation in React components
- **Client-Side Authentication Bypass** (CWE-602): Frontend-only authentication checks

### 🟠 High Severity Issues
- **Sensitive Data Exposure** (CWE-200): API keys and configuration exposed in client code
- **Insecure Data Transmission** (CWE-319): Sensitive data sent over unencrypted connections
- **Missing CSRF Protection** (CWE-352): Form submissions without CSRF tokens

### 🟡 Medium Severity Issues
- **Weak Client-Side Validation** (CWE-20): Client-only input validation
- **Information Disclosure** (CWE-209): Verbose error messages exposing system details
- **Insecure Redirects** (CWE-601): Unvalidated redirect parameters

### 🔵 Additional Educational Vulnerabilities
- **Hardcoded Secrets** (CWE-798): API endpoints and keys in client code
- **Insufficient Logging** (CWE-778): Missing security event logging
- **CORS Misconfigurations** (CWE-942): Overly permissive cross-origin policies

## 🚀 Quick Start

### Prerequisites

- **Node.js** 18.0.0 or higher
- **PNPM** 8.0.0 or higher (recommended) or npm 8.0.0+
- **Docker** (optional for containerized development)

### Development Setup

1. **Install Dependencies**
   ```bash
   cd frontend
   pnpm install
   ```

2. **Start Development Server**
   ```bash
   pnpm dev
   ```

3. **Access Application**
   ```
   http://localhost:3000
   ```

### Production Build

```bash
# Build optimized production bundle
pnpm build

# Start production server
pnpm start
```

### Docker Development

```bash
# Build and run frontend container
docker build -t secland-frontend .
docker run -p 3000:3000 secland-frontend
```

## 🔧 Configuration

### Environment Variables

Create `.env.local` in the frontend directory:

```bash
# API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_BASE_URL=http://localhost:3000

# Security Configuration (Intentionally Weak)
NEXT_PUBLIC_JWT_SECRET=mySecretKey
NEXT_PUBLIC_ENABLE_SECURITY=false

# Educational Flags
NEXT_PUBLIC_SHOW_VULNERABILITIES=true
NEXT_PUBLIC_EDUCATIONAL_MODE=true
```

### TypeScript Configuration

The project uses strict TypeScript configuration for educational clarity:

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "noImplicitReturns": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true
  }
}
```

## 📚 Educational Resources

### Learning Objectives

1. **Frontend Security Fundamentals**
   - Understanding client-side attack vectors
   - Learning secure React development practices
   - Exploring authentication vulnerabilities

2. **Vulnerability Analysis**
   - Identifying XSS attack opportunities
   - Analyzing client-side authentication flaws
   - Understanding CSRF attack scenarios

3. **Secure Development Practices**
   - Implementing proper input validation
   - Securing client-side data storage
   - Managing sensitive information securely

### Attack Scenarios

#### Scenario 1: DOM-Based XSS
```typescript
// Vulnerable component (educational example)
const UnsafeComponent = ({ userInput }) => {
  return <div dangerouslySetInnerHTML={{ __html: userInput }} />
}
```

#### Scenario 2: Client-Side Token Storage
```typescript
// Vulnerable authentication (educational example)
const login = (token: string) => {
  localStorage.setItem('authToken', token) // CWE-922
}
```

#### Scenario 3: Insufficient Client Validation
```typescript
// Weak validation (educational example)
const validateInput = (input: string) => {
  return input.length > 0 // Insufficient validation
}
```

## 🧪 Testing & Security Analysis

### Development Commands

```bash
# Type checking
pnpm type-check

# Linting
pnpm lint
pnpm lint:fix

# Build analysis
pnpm analyze

# Security awareness check
pnpm security-check
```

### Security Testing

The application is designed for security testing with tools like:

- **OWASP ZAP** for automated security scanning
- **Burp Suite** for manual penetration testing
- **Browser DevTools** for client-side vulnerability analysis
- **Custom scripts** for educational vulnerability demonstration

## 🎓 Educational Use Cases

### For Students
- Learn practical frontend security concepts
- Understand React-specific vulnerabilities
- Practice secure coding techniques
- Explore modern development frameworks

### For Instructors
- Demonstrate real-world vulnerability examples
- Teach defensive programming practices
- Assess student understanding of frontend security
- Provide hands-on learning experiences

### For Security Professionals
- Practice frontend penetration testing
- Develop security testing methodologies
- Train junior developers on secure coding
- Research client-side attack vectors

## 📊 Technology Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Framework** | Next.js | 15.2.4 | React framework with SSR |
| **Language** | TypeScript | 5+ | Type-safe development |
| **Styling** | Tailwind CSS | 3+ | Utility-first CSS framework |
| **UI Components** | Radix UI | Latest | Accessible component primitives |
| **Icons** | Lucide React | 0.454+ | Modern icon library |
| **Forms** | React Hook Form | 7.54+ | Form state management |
| **Validation** | Zod | 3.24+ | Runtime type validation |
| **HTTP Client** | Fetch API | Native | API communication |
| **State Management** | React Context | Native | Application state |

## ⚠️ Security Warnings & Compliance

### Educational Use Only
- **NEVER deploy in production environments**
- Use only in controlled, isolated testing environments
- Ensure compliance with local laws and educational policies
- Follow responsible disclosure guidelines for security research

### Risk Mitigation
- Deploy behind firewalls and security controls
- Limit access to authorized educational users only
- Implement comprehensive logging and monitoring
- Establish incident response procedures for training scenarios

### Legal Compliance
- This application is designed exclusively for educational purposes
- Contains intentional vulnerabilities that could be exploited maliciously
- Users are responsible for ethical and legal use
- Unauthorized deployment or misuse is strictly prohibited

## 🔄 Known Limitations

### Educational Focus
- Performance optimized for learning, not production scale
- Simplified business logic for educational clarity
- Limited real-world banking feature implementation
- Intentional security weaknesses throughout the codebase

### Browser Compatibility
- Optimized for modern browsers (Chrome 90+, Firefox 88+, Safari 14+, Edge 90+)
- Requires JavaScript enabled for full functionality
- Basic mobile responsiveness (primarily desktop-focused)
- Progressive Web App features disabled for security research

## 🤝 Contributing

### Development Guidelines
- All code must maintain educational value
- New vulnerabilities must be properly documented with CWE classifications
- Follow TypeScript and React best practices (except for intentional vulnerabilities)
- Maintain clear separation between educational flaws and framework quality

### Code Quality Standards
- Use TypeScript strict mode for non-vulnerable code
- Follow React best practices for component design
- Implement comprehensive documentation
- Maintain consistent code formatting with Prettier

## 📄 License

This project is licensed under the Educational Use License. See the [LICENSE](../LICENSE) file for details.

**Important**: This application contains intentional security vulnerabilities and is designed exclusively for educational purposes. Commercial use, production deployment, or unauthorized distribution is strictly prohibited.

---

## 📞 Support & Resources

### Documentation
- [Next.js Documentation](https://nextjs.org/docs)
- [React Documentation](https://react.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)

### Security Resources
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Common Weakness Enumeration](https://cwe.mitre.org/)
- [React Security Best Practices](https://react.dev/learn/security)
- [Frontend Security Checklist](https://github.com/FallibleInc/security-guide-for-developers)

---

**Educational Notice**: This frontend application is part of a comprehensive vulnerable banking application designed for cybersecurity education. All security vulnerabilities are intentional and documented. Use responsibly and ethically in educational environments only.
