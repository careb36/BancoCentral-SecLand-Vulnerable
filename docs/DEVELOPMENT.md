# Development Setup Guide

## Prerequisites

### System Requirements
- **Java**: OpenJDK 21 or later
- **Node.js**: 18.x or later
- **Docker**: 24.x or later
- **Docker Compose**: 2.x or later
- **Maven**: 3.9.x or later (included via wrapper)
- **PNPM**: Latest version (for frontend package management)

### Operating System Support
- ✅ Windows 10/11
- ✅ macOS 12+ (Intel & Apple Silicon)
- ✅ Ubuntu 20.04+ / Debian 11+
- ✅ RHEL 8+ / CentOS 8+

## Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/careb36/CentralBank-SecLand-Vulnerable.git
cd CentralBank-SecLand-Vulnerable

# Start all services
docker-compose up -d --build

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# Database: localhost:5432
```

### Option 2: Local Development

#### Backend Setup
```bash
# Navigate to project root
cd CentralBank-SecLand-Vulnerable

# Install Java dependencies and run tests
./mvnw clean install

# Run the Spring Boot application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
corepack enable pnpm
pnpm install

# Start development server
pnpm dev
```

#### Database Setup
```bash
# Start PostgreSQL with Docker
docker run --name secland-postgres \
  -e POSTGRES_DB=secland_bank_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15-alpine
```

## Environment Configuration

### Backend Environment Variables
```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/secland_bank_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Security Configuration (Development Only)
LOGGING_LEVEL_COM_SECLAND=DEBUG
```

### Frontend Environment Variables
```env
# API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# Application Configuration
NODE_ENV=development
HOSTNAME=0.0.0.0
PORT=3000

# Security Configuration (Development Only)
NEXT_TELEMETRY_DISABLED=1
```

## Development Workflow

### Code Quality Standards
- **Java**: Follow Google Java Style Guide
- **TypeScript/React**: Follow Airbnb JavaScript Style Guide
- **Commits**: Use Conventional Commits specification
- **Testing**: Minimum 80% code coverage

### Git Workflow
```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "feat: add user authentication endpoint"

# Push and create pull request
git push origin feature/your-feature-name
```

### Testing
```bash
# Backend Tests
./mvnw test

# Frontend Tests
cd frontend
pnpm test

# Integration Tests
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Find process using port
netstat -ano | findstr :3000
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <process_id> /F

# Kill process (Unix)
kill -9 <process_id>
```

#### Database Connection Issues
```bash
# Reset database
docker-compose down -v
docker-compose up -d db
```

#### Frontend Build Issues
```bash
# Clear Next.js cache
cd frontend
rm -rf .next
pnpm build
```

### Performance Optimization
- Use Docker BuildKit for faster builds
- Enable PNPM store for dependency caching
- Configure JVM heap size for large datasets

## IDE Configuration

### Visual Studio Code
Recommended extensions:
- Extension Pack for Java
- ES7+ React/Redux/React-Native snippets
- Prettier - Code formatter
- ESLint
- Tailwind CSS IntelliSense

### IntelliJ IDEA
- Enable Spring Boot support
- Configure Maven auto-import
- Install React/TypeScript plugins

## Security Considerations

⚠️ **WARNING**: This application contains intentional security vulnerabilities for educational purposes.

### Development Security Checklist
- [ ] Use HTTPS in production
- [ ] Validate all environment variables
- [ ] Never commit secrets to version control
- [ ] Use proper CORS configuration
- [ ] Enable security headers
- [ ] Implement rate limiting
- [ ] Use parameterized queries
- [ ] Validate all user inputs

## Support

For development questions and issues:
1. Check the [troubleshooting section](#troubleshooting)
2. Review existing GitHub issues
3. Create a new issue with detailed description
4. Contact the development team

---

**Next Steps**: See [API Documentation](./api-documentation.md) and [Security Assessment](./security-assessment.md)
