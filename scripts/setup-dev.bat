@echo off
REM =============================================================================
REM SecLand Banking Application - Windows Development Setup Script
REM =============================================================================
REM This script provides a quick setup for development environment on Windows
REM Usage: .\scripts\setup-dev.bat
REM =============================================================================

echo 🏦 SecLand Banking Application - Development Setup
echo ==================================================

REM Check Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not installed. Please install Docker Desktop first.
    pause
    exit /b 1
)

REM Check Docker Compose
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker Compose is not installed. Please install Docker Compose first.
    pause
    exit /b 1
)

echo [SUCCESS] Docker and Docker Compose are available

echo.
echo 📋 Setup Options:
echo 1. Full Docker setup (recommended)
echo 2. Local development setup
echo 3. Database only (for local development)
echo 4. Clean and rebuild everything

set /p SETUP_OPTION="Choose an option (1-4): "

if "%SETUP_OPTION%"=="1" goto docker_setup
if "%SETUP_OPTION%"=="2" goto local_setup
if "%SETUP_OPTION%"=="3" goto database_only
if "%SETUP_OPTION%"=="4" goto clean_rebuild

echo [ERROR] Invalid option selected
pause
exit /b 1

:docker_setup
echo [INFO] Setting up full Docker environment...

REM Stop any running containers
echo [INFO] Stopping existing containers...
docker-compose down

REM Build and start containers
echo [INFO] Building and starting containers...
docker-compose up -d --build

REM Wait for services to be ready
echo [INFO] Waiting for services to start...
timeout /t 30 /nobreak >nul

echo.
echo 🎉 Setup completed!
echo 🌐 Frontend: http://localhost:3000
echo 🔧 Backend API: http://localhost:8080
echo 🗄️ Database: localhost:5432
echo.
echo 📚 Test users:
echo    Username: testuser    ^| Password: password123
echo    Username: admin       ^| Password: admin123
echo    Username: carolina_p  ^| Password: carolina123
goto end

:local_setup
echo [INFO] Setting up local development environment...

REM Start database only
echo [INFO] Starting PostgreSQL database...
docker-compose up -d db

REM Wait for database
echo [INFO] Waiting for database to be ready...
timeout /t 10 /nobreak >nul

REM Backend setup
if exist "mvnw.cmd" (
    echo [INFO] Setting up backend dependencies...
    call mvnw.cmd clean install -DskipTests
    echo [SUCCESS] Backend dependencies installed
) else (
    echo [WARNING] Maven wrapper not found. Please install dependencies manually.
)

REM Frontend setup
if exist "frontend" (
    echo [INFO] Setting up frontend dependencies...
    cd frontend
    
    where pnpm >nul 2>&1
    if %errorlevel% equ 0 (
        pnpm install
        echo [SUCCESS] Frontend dependencies installed with pnpm
    ) else (
        where npm >nul 2>&1
        if %errorlevel% equ 0 (
            npm install
            echo [SUCCESS] Frontend dependencies installed with npm
        ) else (
            echo [WARNING] Neither pnpm nor npm found. Please install Node.js first.
        )
    )
    
    cd ..
)

echo.
echo 🎉 Local development setup completed!
echo.
echo 📋 Next steps:
echo 1. Start backend: .\mvnw.cmd spring-boot:run
echo 2. Start frontend: cd frontend ^&^& pnpm dev
echo 3. Access application at http://localhost:3000
goto end

:database_only
echo [INFO] Starting database only...
docker-compose up -d db

echo [INFO] Waiting for database to be ready...
timeout /t 10 /nobreak >nul

echo [SUCCESS] Database is ready!
echo 🗄️ Database connection: localhost:5432
echo 📊 Database: secland_bank_db
echo 👤 Username: postgres
echo 🔑 Password: password
goto end

:clean_rebuild
echo [INFO] Cleaning and rebuilding everything...

REM Stop and remove containers
echo [INFO] Stopping and removing containers...
docker-compose down -v

REM Remove images and build fresh
echo [INFO] Rebuilding images...
docker-compose build --no-cache

REM Start fresh
echo [INFO] Starting fresh containers...
docker-compose up -d

REM Wait for services
echo [INFO] Waiting for services to start...
timeout /t 30 /nobreak >nul

echo [SUCCESS] Clean rebuild completed!
echo 🌐 Frontend: http://localhost:3000
echo 🔧 Backend API: http://localhost:8080
goto end

:end
echo.
echo [SUCCESS] Setup completed successfully!
echo.
echo 📖 Useful commands:
echo    docker-compose logs          # View all logs
echo    docker-compose logs frontend # View frontend logs
echo    docker-compose logs app      # View backend logs
echo    docker-compose ps            # Check container status
echo    docker-compose down          # Stop all services
echo.
echo 📚 Documentation:
echo    README.md                    # Project overview
echo    docs\DEVELOPMENT.md          # Development guide
echo    docs\API.md                  # API documentation
echo    docs\SECURITY.md             # Security assessment
echo.
echo [WARNING] ⚠️ Remember: This is an intentionally vulnerable application for educational use only!

pause
