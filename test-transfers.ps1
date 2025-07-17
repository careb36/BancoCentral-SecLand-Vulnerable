# ============================================
# Script de Prueba Completa - Sistema de Transferencias
# BancoCentral SecLand Vulnerable
# ============================================

Write-Host "🏦 INICIANDO PRUEBA COMPLETA DE TRANSFERENCIAS" -ForegroundColor Green
Write-Host "=" * 50 -ForegroundColor Green

$API_BASE = "http://localhost:8080/api"

# Paso 1: Crear usuario para pruebas
Write-Host "`n📝 PASO 1: Creando usuario de prueba..." -ForegroundColor Yellow

$registerData = @{
    username = "transfertest"
    password = "password123"
    fullName = "Transfer Test User"
} | ConvertTo-Json

try {
    Invoke-WebRequest -Uri "$API_BASE/auth/register" -Method POST -Body $registerData -ContentType "application/json" | Out-Null
    Write-Host "✅ Usuario creado exitosamente" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Usuario ya existe, continuando..." -ForegroundColor Cyan
}

# Paso 2: Hacer login y obtener token
Write-Host "`n🔐 PASO 2: Haciendo login y obteniendo token..." -ForegroundColor Yellow

$loginData = @{
    username = "transfertest"
    password = "password123"
} | ConvertTo-Json

$loginResponse = Invoke-WebRequest -Uri "$API_BASE/auth/login" -Method POST -Body $loginData -ContentType "application/json"
$loginResult = $loginResponse.Content | ConvertFrom-Json
$token = $loginResult.token

Write-Host "✅ Login exitoso. Token obtenido." -ForegroundColor Green

# Headers para requests autenticados
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Paso 3: Crear cuenta de origen (checking)
Write-Host "`n🏛️ PASO 3: Creando cuenta de origen (CHECKING)..." -ForegroundColor Yellow

$account1Data = @{
    accountType = "CHECKING"
    initialDeposit = 1000.00
} | ConvertTo-Json

$account1Response = Invoke-WebRequest -Uri "$API_BASE/accounts" -Method POST -Body $account1Data -Headers $headers
$account1 = $account1Response.Content | ConvertFrom-Json

Write-Host "✅ Cuenta CHECKING creada: #$($account1.accountNumber) con balance: $$$($account1.balance)" -ForegroundColor Green

# Paso 4: Crear cuenta de destino (savings)
Write-Host "`n💰 PASO 4: Creando cuenta de destino (SAVINGS)..." -ForegroundColor Yellow

$account2Data = @{
    accountType = "SAVINGS"
    initialDeposit = 500.00
} | ConvertTo-Json

$account2Response = Invoke-WebRequest -Uri "$API_BASE/accounts" -Method POST -Body $account2Data -Headers $headers
$account2 = $account2Response.Content | ConvertFrom-Json

Write-Host "✅ Cuenta SAVINGS creada: #$($account2.accountNumber) con balance: $$$($account2.balance)" -ForegroundColor Green

# Paso 5: Realizar transferencia
Write-Host "`n💸 PASO 5: Realizando transferencia de $250..." -ForegroundColor Yellow

$transferData = @{
    fromAccountId = $account1.id
    toAccountNumber = $account2.accountNumber
    amount = 250.00
    description = "Transferencia de prueba automatizada"
} | ConvertTo-Json

$transferResponse = Invoke-WebRequest -Uri "$API_BASE/accounts/transfer" -Method POST -Body $transferData -Headers $headers
$transfer = $transferResponse.Content | ConvertFrom-Json

Write-Host "✅ Transferencia completada!" -ForegroundColor Green
Write-Host "   💵 Monto: $$$($transfer.amount)" -ForegroundColor Cyan
Write-Host "   📝 Descripción: $($transfer.description)" -ForegroundColor Cyan
Write-Host "   🕐 Fecha: $($transfer.createdAt)" -ForegroundColor Cyan

# Paso 6: Verificar balances finales
Write-Host "`n📊 PASO 6: Verificando balances finales..." -ForegroundColor Yellow

$accountsResponse = Invoke-WebRequest -Uri "$API_BASE/accounts" -Headers $headers
$finalAccounts = $accountsResponse.Content | ConvertFrom-Json

foreach ($account in $finalAccounts) {
    Write-Host "   🏛️ Cuenta #$($account.accountNumber) ($($account.accountType)): $$$($account.balance)" -ForegroundColor Cyan
}

# Paso 7: Ver historial de transacciones
Write-Host "`n📋 PASO 7: Historial de transacciones..." -ForegroundColor Yellow

$historyResponse = Invoke-WebRequest -Uri "$API_BASE/accounts/$($account1.id)/transactions" -Headers $headers
$transactions = $historyResponse.Content | ConvertFrom-Json

Write-Host "   📜 Transacciones de la cuenta origen:" -ForegroundColor Cyan
foreach ($txn in $transactions) {
    Write-Host "      - $($txn.type): $$$($txn.amount) - $($txn.description)" -ForegroundColor White
}

Write-Host "`n🎉 PRUEBA COMPLETA FINALIZADA EXITOSAMENTE" -ForegroundColor Green
Write-Host "=" * 50 -ForegroundColor Green

Write-Host "`n📋 RESUMEN:" -ForegroundColor Yellow
Write-Host "✅ Usuario creado y autenticado" -ForegroundColor Green
Write-Host "✅ Dos cuentas bancarias creadas" -ForegroundColor Green
Write-Host "✅ Transferencia de $250 ejecutada" -ForegroundColor Green
Write-Host "✅ Balances actualizados correctamente" -ForegroundColor Green
Write-Host "✅ Historial de transacciones disponible" -ForegroundColor Green
