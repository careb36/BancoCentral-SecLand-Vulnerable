# 🧪 Plan de Pruebas - BancoCentral SecLand Vulnerable

## 📅 Fecha: 17 de Julio, 2025
## 🎯 Objetivo: Verificar funcionalidad completa y documentar vulnerabilidades educativas

---

## 1️⃣ PRUEBAS DE FUNCIONALIDAD BÁSICA

### 🔐 Autenticación
- [ ] **Registro de usuario nuevo**
  - Usuario: `test.user.2025`
  - Email: `test@example.com`
  - Password: `TestPassword123!`
  
- [ ] **Login con usuarios existentes**
  - `testuser` / `password123`
  - `rebeca.pereira` / `test123`
  - `carolina.fernandez` / `carolina123`
  - `admin` / `admin123`

- [ ] **Logout y persistencia de sesión**

### 🏦 Gestión de Cuentas
- [ ] **Crear cuenta Savings** (Depósito inicial: $1000)
- [ ] **Crear cuenta Checking** (Depósito inicial: $500)
- [ ] **Visualizar lista de cuentas**
- [ ] **Eliminar cuenta** (solo con balance 0)

### 💰 Operaciones Bancarias
- [ ] **Depósito**: $200 en cuenta Savings
- [ ] **Transferencia interna**: $100 entre cuentas propias
- [ ] **Transferencia externa**: $50 a cuenta de otro usuario
- [ ] **Historial de transacciones**: Verificar movimientos

---

## 2️⃣ PRUEBAS DE VULNERABILIDADES EDUCATIVAS

### 🔓 IDOR (Insecure Direct Object Reference)
- [ ] **Acceso a cuentas ajenas**
  - Endpoint: `GET /api/accounts/user/{username}`
  - Probar con username de otro usuario
  
- [ ] **Transferencias desde cuentas ajenas**
  - Modificar `fromAccountId` en payload
  - Intentar transferir desde cuenta que no es propia

- [ ] **Acceso a transacciones ajenas**
  - Endpoint: `GET /api/accounts/{accountId}/transactions`
  - Usar ID de cuenta ajena

### 🧨 XSS (Cross-Site Scripting)
- [ ] **XSS en descripción de transferencia**
  - Descripción: `<script>alert('XSS!')</script>`
  - Verificar si se ejecuta en historial

- [ ] **XSS en nombre de usuario**
  - Registro con: `<img src=x onerror=alert('XSS')>`

### 🔑 Autenticación Débil
- [ ] **Passwords débiles**
  - Probar: `123`, `password`, `admin`
  
- [ ] **Enumeración de usuarios**
  - Mensajes diferentes para usuario existente vs inexistente

---

## 3️⃣ PRUEBAS DE RENDIMIENTO Y STRESS

### 📊 Carga de Datos
- [ ] **Múltiples transferencias simultáneas**
- [ ] **Creación masiva de cuentas**
- [ ] **Consultas intensivas de historial**

---

## 4️⃣ CONFIGURACIÓN DE ENTORNOS

### 🐳 Docker Setup
- [ ] **Levantar stack completo**: `docker-compose up -d`
- [ ] **Verificar healthchecks**: DB, Backend, Frontend
- [ ] **Acceso web**: `http://localhost`

### 💻 Local Development Setup  
- [ ] **Backend Spring Boot**: Puerto 8080 con H2
- [ ] **Frontend local**: Archivo HTML directo
- [ ] **Console H2**: `http://localhost:8080/h2-console`

---

## 5️⃣ DOCUMENTACIÓN DE RESULTADOS

### ✅ Funcionalidades Operativas
- Autenticación: ⏳ Pendiente
- Gestión de cuentas: ⏳ Pendiente  
- Operaciones bancarias: ⏳ Pendiente

### 🔓 Vulnerabilidades Confirmadas
- IDOR: ⏳ Pendiente
- XSS: ⏳ Pendiente
- Weak Auth: ⏳ Pendiente

### 🐛 Bugs Encontrados
- Ninguno reportado aún

---

## 📋 CHECKLIST DE EJECUCIÓN

1. ✅ Levantar aplicación (Docker o Local)
2. ⏳ Ejecutar pruebas de funcionalidad
3. ⏳ Documentar vulnerabilidades
4. ⏳ Generar reporte final

**Estado actual**: ✅ **Aplicación levantada con Docker** - Iniciando pruebas funcionales

### 🔄 Estado de Infraestructura
- ✅ **Docker Stack**: 3 contenedores activos
- ✅ **Frontend**: http://localhost (Nginx)
- ✅ **Backend**: Spring Boot + PostgreSQL
- ✅ **Red**: secland_banking_network operativa
