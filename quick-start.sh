#!/bin/bash
# 🚀 Script de Inicio Rápido - BancoCentral SecLand
# Ejecutar después de reiniciar Docker Desktop

echo "🐳 INICIANDO BANCO CENTRAL SECLAND..."
echo "======================================"

# Verificar que Docker esté funcionando
echo "🔍 Verificando Docker..."
docker --version
if [ $? -ne 0 ]; then
    echo "❌ ERROR: Docker no está funcionando"
    echo "💡 Solución: Asegúrate de que Docker Desktop esté iniciado"
    exit 1
fi

# Limpiar contenedores previos
echo "🧹 Limpiando contenedores previos..."
docker-compose down 2>/dev/null

# Iniciar el stack completo
echo "🚀 Iniciando stack de 3 contenedores..."
docker-compose up -d

# Esperar que los servicios estén listos
echo "⏳ Esperando que los servicios estén listos..."
sleep 10

# Verificar estado de contenedores
echo "📊 Estado de contenedores:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Verificar conectividad
echo ""
echo "🧪 Verificando conectividad..."

# Test frontend
curl -s -o /dev/null -w "Frontend (Nginx): %{http_code}\n" http://localhost

# Test backend
curl -s -o /dev/null -w "Backend (Spring Boot): %{http_code}\n" http://localhost:8080/actuator/health

echo ""
echo "✅ APLICACIÓN LISTA PARA USAR"
echo "=============================="
echo "🌐 Frontend: http://localhost"
echo "🔧 Backend API: http://localhost:8080/api"
echo "📊 Health Check: http://localhost:8080/actuator/health"
echo ""
echo "🧪 Para ejecutar pruebas automáticas:"
echo "    python test_bank_api.py"
echo ""
echo "📋 Para ver el reporte completo:"
echo "    cat TESTING_REPORT.md"
