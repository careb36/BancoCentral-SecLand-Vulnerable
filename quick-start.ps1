# 🚀 Script de Inicio Rápido - BancoCentral SecLand (PowerShell)
# Ejecutar después de reiniciar Docker Desktop

Write-Host "🐳 INICIANDO BANCO CENTRAL SECLAND..." -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

# Verificar que Docker esté funcionando
Write-Host "🔍 Verificando Docker..." -ForegroundColor Yellow
try {
    docker --version
    Write-Host "✅ Docker funcionando correctamente" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR: Docker no está funcionando" -ForegroundColor Red
    Write-Host "💡 Solución: Asegúrate de que Docker Desktop esté iniciado" -ForegroundColor Yellow
    exit 1
}

# Limpiar contenedores previos
Write-Host "🧹 Limpiando contenedores previos..." -ForegroundColor Yellow
docker-compose down 2>$null

# Iniciar el stack completo
Write-Host "🚀 Iniciando stack de 3 contenedores..." -ForegroundColor Yellow
docker-compose up -d

# Esperar que los servicios estén listos
Write-Host "⏳ Esperando que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar estado de contenedores
Write-Host "📊 Estado de contenedores:" -ForegroundColor Cyan
docker ps --format "table {{.Names}}`t{{.Status}}`t{{.Ports}}"

# Verificar conectividad
Write-Host ""
Write-Host "🧪 Verificando conectividad..." -ForegroundColor Yellow

try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost" -UseBasicParsing -TimeoutSec 5
    Write-Host "Frontend (Nginx): $($frontendResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Frontend (Nginx): ERROR" -ForegroundColor Red
}

try {
    $backendResponse = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "Backend (Spring Boot): $($backendResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Backend (Spring Boot): ERROR" -ForegroundColor Red
}

Write-Host ""
Write-Host "✅ APLICACIÓN LISTA PARA USAR" -ForegroundColor Green
Write-Host "==============================" -ForegroundColor Green
Write-Host "🌐 Frontend: http://localhost" -ForegroundColor Cyan
Write-Host "🔧 Backend API: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "📊 Health Check: http://localhost:8080/actuator/health" -ForegroundColor Cyan
Write-Host ""
Write-Host "🧪 Para ejecutar pruebas automáticas:" -ForegroundColor Yellow
Write-Host "    python test_bank_api.py" -ForegroundColor White
Write-Host ""
Write-Host "📋 Para ver el reporte completo:" -ForegroundColor Yellow
Write-Host "    Get-Content TESTING_REPORT.md" -ForegroundColor White
