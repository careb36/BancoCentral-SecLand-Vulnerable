#!/usr/bin/env python3
"""
Script de pruebas sistemáticas para BancoCentral-SecLand-Vulnerable
Basado en TESTING_PLAN.md

Ejecuta pruebas de:
1. Autenticación (registro, login)
2. Operaciones bancarias
3. Vulnerabilidades de seguridad
"""

import requests
import json
import time
import sys
from datetime import datetime

# Configuración
BASE_URL = "http://localhost:8080/api"
FRONTEND_URL = "http://localhost"

class Colors:
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    CYAN = '\033[96m'
    WHITE = '\033[97m'
    RESET = '\033[0m'

class BankTestSuite:
    def __init__(self):
        self.session = requests.Session()
        self.test_user = None
        self.auth_token = None
        self.user_id = None
        
    def print_header(self, title):
        print(f"\n{Colors.GREEN}{'='*60}{Colors.RESET}")
        print(f"{Colors.GREEN}{title:^60}{Colors.RESET}")
        print(f"{Colors.GREEN}{'='*60}{Colors.RESET}")
        
    def print_test(self, test_name):
        print(f"\n{Colors.CYAN}🔄 {test_name}{Colors.RESET}")
        
    def print_success(self, message):
        print(f"{Colors.GREEN}✅ {message}{Colors.RESET}")
        
    def print_error(self, message):
        print(f"{Colors.RED}❌ {message}{Colors.RESET}")
        
    def print_warning(self, message):
        print(f"{Colors.YELLOW}⚠️  {message}{Colors.RESET}")

    def test_registration(self):
        """Test 1.1: Registro de usuario"""
        self.print_test("Test 1.1: Registro de usuario")
        
        # Generar usuario único (máximo 20 caracteres)
        timestamp = datetime.now().strftime("%H%M%S")
        username = f"test{timestamp}"  # Formato: test164635 (10 chars)
        
        user_data = {
            "username": username,
            "fullName": f"Test User {timestamp}",
            "password": "TestPassword123!"
        }
        
        try:
            response = self.session.post(
                f"{BASE_URL}/auth/register",
                json=user_data,
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code in [200, 201]:  # 201 = Created
                data = response.json()
                self.test_user = username
                self.user_id = data.get('id')
                self.print_success(f"Usuario registrado: {data.get('username')} (ID: {data.get('id')})")
                return True
            else:
                self.print_error(f"Error {response.status_code}: {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción: {str(e)}")
            return False
    
    def test_login(self):
        """Test 1.2: Login de usuario"""
        self.print_test("Test 1.2: Login de usuario")
        
        if not self.test_user:
            self.print_error("No hay usuario de prueba registrado")
            return False
            
        login_data = {
            "username": self.test_user,
            "password": "TestPassword123!"
        }
        
        try:
            response = self.session.post(
                f"{BASE_URL}/auth/login",
                json=login_data,
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code in [200, 201]:  # 200 = OK, 201 = Created
                data = response.json()
                self.auth_token = data.get('token')
                self.print_success(f"Login exitoso. Token obtenido: {self.auth_token[:20]}...")
                return True
            else:
                self.print_error(f"Error {response.status_code}: {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción: {str(e)}")
            return False

    def test_list_accounts(self):
        """Test 2.2: Listar cuentas del usuario"""
        self.print_test("Test 2.2: Listar cuentas del usuario")
        
        if not self.auth_token:
            self.print_error("No hay token de autenticación")
            return False
            
        headers = {
            "Authorization": f"Bearer {self.auth_token}"
        }
        
        try:
            response = self.session.get(
                f"{BASE_URL}/accounts",
                headers=headers
            )
            
            if response.status_code in [200, 201]:
                data = response.json()
                self.print_success(f"Usuario tiene {len(data)} cuentas")
                for account in data:
                    print(f"   - {account.get('accountNumber')}: ${account.get('balance')} ({account.get('accountType')})")
                return True
            else:
                self.print_error(f"Error {response.status_code}: {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción: {str(e)}")
            return False

    def test_vulnerability_idor_accounts(self):
        """Test 3.1: IDOR - Acceso a cuentas de otros usuarios"""
        self.print_test("Test 3.1: IDOR - Acceso a cuentas de otros usuarios")
        
        if not self.auth_token:
            self.print_error("No hay token de autenticación")
            return False
            
        headers = {
            "Authorization": f"Bearer {self.auth_token}"
        }
        
        # Intentar acceder a cuentas de usuario "admin" (vulnerable IDOR)
        try:
            response = self.session.get(
                f"{BASE_URL}/accounts/user/admin",
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                self.print_warning(f"¡VULNERABILIDAD CONFIRMADA! Acceso a {len(data)} cuentas de 'admin'")
                for account in data:
                    print(f"   - {account.get('accountNumber')}: ${account.get('balance')} ({account.get('accountType')})")
                return True
            else:
                self.print_success("Acceso denegado correctamente (no vulnerable)")
                return True
                
        except Exception as e:
            self.print_error(f"Excepción: {str(e)}")
            return False

    def test_health_check(self):
        """Test 0: Verificación de salud del sistema"""
        self.print_test("Test 0: Health Check del sistema")
        
        try:
            # Test backend
            response = self.session.get(f"{BASE_URL.replace('/api', '')}/actuator/health")
            if response.status_code == 200:
                self.print_success("Backend (Spring Boot) funcionando")
            else:
                self.print_error(f"Backend error: {response.status_code}")
                
            # Test frontend
            response = self.session.get(FRONTEND_URL)
            if response.status_code == 200:
                self.print_success("Frontend (Nginx) funcionando")
            else:
                self.print_error(f"Frontend error: {response.status_code}")
                
            return True
            
        except Exception as e:
            self.print_error(f"Error de conectividad: {str(e)}")
            return False

    def run_authentication_tests(self):
        """Ejecutar todas las pruebas de autenticación"""
        self.print_header("SECCIÓN 1: PRUEBAS DE AUTENTICACIÓN")
        
        success_count = 0
        total_tests = 2
        
        if self.test_registration():
            success_count += 1
            
        if self.test_login():
            success_count += 1
            
        print(f"\n{Colors.YELLOW}📊 Resultados Sección 1: {success_count}/{total_tests} tests exitosos{Colors.RESET}")
        return success_count == total_tests

    def run_banking_tests(self):
        """Ejecutar todas las pruebas bancarias"""
        self.print_header("SECCIÓN 2: PRUEBAS DE OPERACIONES BANCARIAS")
        
        success_count = 0
        total_tests = 1
        
        if self.test_list_accounts():
            success_count += 1
            
        print(f"\n{Colors.YELLOW}📊 Resultados Sección 2: {success_count}/{total_tests} tests exitosos{Colors.RESET}")
        
        # Ejecutar pruebas de vulnerabilidades si las pruebas básicas fueron exitosas
        if success_count > 0:
            self.run_vulnerability_tests()
        
        return success_count == total_tests

    def run_vulnerability_tests(self):
        """Ejecutar pruebas de vulnerabilidades"""
        self.print_header("SECCIÓN 3: PRUEBAS DE VULNERABILIDADES")
        
        success_count = 0
        total_tests = 3
        
        if self.test_vulnerability_idor_accounts():
            success_count += 1
            
        if self.test_vulnerability_idor_transactions():
            success_count += 1
            
        if self.test_vulnerability_idor_deposit():
            success_count += 1
            
        print(f"\n{Colors.YELLOW}📊 Resultados Sección 3: {success_count}/{total_tests} tests ejecutados{Colors.RESET}")
        return success_count == total_tests

    def test_vulnerability_idor_transactions(self):
        """Test 3.2: IDOR - Acceso a transacciones de cuentas ajenas"""
        self.print_test("Test 3.2: IDOR - Acceso a transacciones de cuentas ajenas")
        
        if not self.auth_token:
            self.print_error("No hay token de autenticación")
            return False
            
        headers = {
            "Authorization": f"Bearer {self.auth_token}"
        }
        
        # Intentar acceder a transacciones de la cuenta del admin (ID 502 según los logs)
        target_account_id = 502
        try:
            response = self.session.get(
                f"{BASE_URL}/accounts/{target_account_id}/transactions",
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                self.print_warning(f"¡VULNERABILIDAD CONFIRMADA! Acceso a {len(data)} transacciones de cuenta {target_account_id}")
                for i, transaction in enumerate(data[:3]):  # Mostrar solo las primeras 3
                    print(f"   - Transacción {i+1}: ${transaction.get('amount')} - {transaction.get('description')}")
                if len(data) > 3:
                    print(f"   ... y {len(data) - 3} transacciones más")
                return True
            else:
                self.print_success("Acceso denegado correctamente (no vulnerable)")
                return True
                
        except Exception as e:
            self.print_error(f"Excepción: {str(e)}")
            return False

    def test_vulnerability_idor_deposit(self):
        """Test 3.3: IDOR - Depósito en cuenta ajena"""
        self.print_test("Test 3.3: IDOR - Depósito en cuenta ajena")
        
        if not self.auth_token:
            self.print_error("No hay token de autenticación")
            return False
            
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.auth_token}"
        }
        
        # Intentar depositar en cuenta del admin (ID 502)
        target_account_id = 502
        deposit_data = {
            "amount": 0.01  # Depósito mínimo para probar
        }
        
        try:
            response = self.session.post(
                f"{BASE_URL}/accounts/{target_account_id}/deposit",
                json=deposit_data,
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                self.print_warning(f"¡VULNERABILIDAD CONFIRMADA! Depósito exitoso en cuenta {target_account_id}")
                print(f"   - Nuevo balance: ${data.get('balance')}")
                return True
            else:
                self.print_success("Depósito denegado correctamente (no vulnerable)")
                return True
                
        except Exception as e:
            self.print_error(f"Excepción: {str(e)}")
            return False

    def run_all_tests(self):
        """Ejecutar suite completa de pruebas"""
        self.print_header("🧪 PLAN DE PRUEBAS SISTEMÁTICO - BANCO CENTRAL SECLAND")
        
        print(f"{Colors.WHITE}Iniciando pruebas automáticas...{Colors.RESET}")
        print(f"{Colors.WHITE}Base URL: {BASE_URL}{Colors.RESET}")
        print(f"{Colors.WHITE}Frontend URL: {FRONTEND_URL}{Colors.RESET}")
        
        # Test de conectividad
        if not self.test_health_check():
            self.print_error("Sistema no disponible. Abortando pruebas.")
            return False
        
        # Tests de autenticación
        auth_success = self.run_authentication_tests()
        
        # Tests bancarios (solo si auth fue exitoso)
        banking_success = False
        vulnerability_success = False
        if auth_success:
            banking_success = self.run_banking_tests()
        else:
            self.print_warning("Saltando pruebas bancarias debido a fallas en autenticación")
        
        # Resumen final
        self.print_header("📋 RESUMEN FINAL")
        if auth_success:
            self.print_success("✅ Autenticación: EXITOSA")
        else:
            self.print_error("❌ Autenticación: FALLIDA")
            
        if banking_success:
            self.print_success("✅ Operaciones Bancarias: EXITOSAS")
        elif auth_success:
            self.print_error("❌ Operaciones Bancarias: FALLIDAS")
        else:
            self.print_warning("⚠️  Operaciones Bancarias: NO EJECUTADAS")
        
        return auth_success and banking_success

if __name__ == "__main__":
    suite = BankTestSuite()
    success = suite.run_all_tests()
    
    if success:
        print(f"\n{Colors.GREEN}🎉 TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE{Colors.RESET}")
        sys.exit(0)
    else:
        print(f"\n{Colors.RED}💥 ALGUNAS PRUEBAS FALLARON{Colors.RESET}")
        sys.exit(1)
