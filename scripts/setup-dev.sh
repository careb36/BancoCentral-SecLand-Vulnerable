#!/bin/bash

# =============================================================================
# SecLand Banking Application - Development Setup Script
# =============================================================================
# This script provides a quick setup for development environment
# Usage: ./scripts/setup-dev.sh
# =============================================================================

set -e  # Exit on any error

echo "🏦 SecLand Banking Application - Development Setup"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
print_status "Checking prerequisites..."

# Check Docker
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check Docker Compose
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check Java (optional for Docker deployment)
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "([^"]*)"' | head -1 | cut -d'"' -f2)
    print_success "Java $JAVA_VERSION detected"
else
    print_warning "Java not found. Required for local development (not needed for Docker)"
fi

# Check Node.js (optional for Docker deployment)
if command -v node &> /dev/null; then
    NODE_VERSION=$(node --version)
    print_success "Node.js $NODE_VERSION detected"
else
    print_warning "Node.js not found. Required for local frontend development"
fi

print_success "Prerequisites check completed"

# Setup options
echo ""
echo "📋 Setup Options:"
echo "1. Full Docker setup (recommended)"
echo "2. Local development setup"
echo "3. Database only (for local development)"
echo "4. Clean and rebuild everything"

read -p "Choose an option (1-4): " SETUP_OPTION

case $SETUP_OPTION in
    1)
        print_status "Setting up full Docker environment..."
        
        # Stop any running containers
        print_status "Stopping existing containers..."
        docker-compose down || true
        
        # Build and start containers
        print_status "Building and starting containers..."
        docker-compose up -d --build
        
        # Wait for services to be ready
        print_status "Waiting for services to start..."
        sleep 30
        
        # Check service health
        print_status "Checking service health..."
        if docker-compose ps | grep -q "healthy"; then
            print_success "All services are healthy!"
        else
            print_warning "Some services may not be fully ready. Check logs with: docker-compose logs"
        fi
        
        echo ""
        echo "🎉 Setup completed!"
        echo "🌐 Frontend: http://localhost:3000"
        echo "🔧 Backend API: http://localhost:8080"
        echo "🗄️  Database: localhost:5432"
        echo ""
        echo "📚 Test users:"
        echo "   Username: testuser    | Password: password123"
        echo "   Username: admin       | Password: admin123"
        echo "   Username: carolina_p  | Password: carolina123"
        ;;
        
    2)
        print_status "Setting up local development environment..."
        
        # Start database only
        print_status "Starting PostgreSQL database..."
        docker-compose up -d db
        
        # Wait for database
        print_status "Waiting for database to be ready..."
        sleep 10
        
        # Backend setup
        if command -v ./mvnw &> /dev/null; then
            print_status "Setting up backend dependencies..."
            ./mvnw clean install -DskipTests
            print_success "Backend dependencies installed"
        else
            print_warning "Maven wrapper not found. Please install dependencies manually."
        fi
        
        # Frontend setup
        if [ -d "frontend" ]; then
            print_status "Setting up frontend dependencies..."
            cd frontend
            
            if command -v pnpm &> /dev/null; then
                pnpm install
                print_success "Frontend dependencies installed with pnpm"
            elif command -v npm &> /dev/null; then
                npm install
                print_success "Frontend dependencies installed with npm"
            else
                print_warning "Neither pnpm nor npm found. Please install Node.js first."
            fi
            
            cd ..
        fi
        
        echo ""
        echo "🎉 Local development setup completed!"
        echo ""
        echo "📋 Next steps:"
        echo "1. Start backend: ./mvnw spring-boot:run"
        echo "2. Start frontend: cd frontend && pnpm dev"
        echo "3. Access application at http://localhost:3000"
        ;;
        
    3)
        print_status "Starting database only..."
        docker-compose up -d db
        
        print_status "Waiting for database to be ready..."
        sleep 10
        
        print_success "Database is ready!"
        echo "🗄️  Database connection: localhost:5432"
        echo "📊 Database: secland_bank_db"
        echo "👤 Username: postgres"
        echo "🔑 Password: password"
        ;;
        
    4)
        print_status "Cleaning and rebuilding everything..."
        
        # Stop and remove containers
        print_status "Stopping and removing containers..."
        docker-compose down -v
        
        # Remove images
        print_status "Removing existing images..."
        docker-compose build --no-cache
        
        # Start fresh
        print_status "Starting fresh containers..."
        docker-compose up -d
        
        # Wait for services
        print_status "Waiting for services to start..."
        sleep 30
        
        print_success "Clean rebuild completed!"
        echo "🌐 Frontend: http://localhost:3000"
        echo "🔧 Backend API: http://localhost:8080"
        ;;
        
    *)
        print_error "Invalid option selected"
        exit 1
        ;;
esac

echo ""
print_success "Setup completed successfully!"
echo ""
echo "📖 Useful commands:"
echo "   docker-compose logs          # View all logs"
echo "   docker-compose logs frontend # View frontend logs"
echo "   docker-compose logs app      # View backend logs"
echo "   docker-compose ps            # Check container status"
echo "   docker-compose down          # Stop all services"
echo ""
echo "📚 Documentation:"
echo "   README.md                    # Project overview"
echo "   docs/DEVELOPMENT.md          # Development guide"
echo "   docs/API.md                  # API documentation"
echo "   docs/SECURITY.md             # Security assessment"
echo ""
print_warning "⚠️  Remember: This is an intentionally vulnerable application for educational use only!"
