# BancoCentral-SecLand-Vulnerable - Aplicación Bancaria Deliberadamente Vulnerable

Este repositorio contiene el código fuente de **"Banco Central de Securitilandia"**, una aplicación web de banca básica desarrollada con Spring Boot (Java) y MariaDB. El propósito principal de este proyecto es servir como un **laboratorio deliberadamente vulnerable** para la investigación y práctica de hacking ético, así como para el desarrollo de sistemas de detección de anomalías basados en Inteligencia Artificial, en el contexto de un Trabajo de Fin de Máster (TFM).

## 🚀 Objetivo del Proyecto

El objetivo de "Banco Central de Securitilandia" es proporcionar un entorno controlado y reproducible para:
* Realizar pruebas de penetración y ejercicios de hacking ético sobre funcionalidades bancarias comunes.
* Estudiar y explotar vulnerabilidades de seguridad intencionalmente introducidas en el código y la configuración.
* Recolectar logs detallados de actividad del sistema bajo condiciones normales y de ataque para el entrenamiento y validación de modelos de Inteligencia Artificial dedicados a la detección de anomalías.
* Servir como una plataforma de investigación original para un TFM, garantizando la ausencia de "solucionarios" públicos para sus vulnerabilidades específicas.

## ✨ Características Principales

* **Gestión de Usuarios:** Registro y autenticación de clientes bancarios.
* **Gestión de Cuentas:** Apertura y consulta de cuentas bancarias.
* **Transacciones Básicas:** Transferencias de fondos entre cuentas.
* **API RESTful:** Todos los servicios expuestos a través de una API RESTful.
* **Logging Detallado:** Configuración de logs para capturar eventos de seguridad relevantes.

## ⚠️ Vulnerabilidades Intencionales

Esta aplicación ha sido diseñada con **vulnerabilidades de seguridad conocidas y comunes en sistemas financieros**, introducidas deliberadamente para fines de estudio. **No debe ser utilizada en entornos de producción.**

Las vulnerabilidades clave que se pueden esperar auditar y explotar incluyen (pero no se limitan a):
* **SQL Injection:** En puntos de autenticación y/o consulta de datos.
* **Broken Authentication / Sesión Insegura:** Fallos en la gestión de credenciales y sesiones.
* **Insecure Direct Object Reference (IDOR):** Permisos de acceso inadecuados que exponen recursos de otros usuarios.
* **Logic Flaws:** Errores en la lógica de negocio que permiten abusos (ej., transferencias sin validación de saldo).
* **Exposición de Datos Sensibles:** Información que no debería ser visible en respuestas de la API o logs.
* **Configuraciones Inseguras:** Puertos expuestos innecesariamente o configuraciones por defecto.

Un documento detallado, `VULNERABILITIES.md`, dentro de este repositorio, explicará cada una de estas vulnerabilidades, cómo se introdujeron y cómo pueden ser explotadas.

## 🛠️ Tecnologías Utilizadas

* **Backend:** Java 21, Spring Boot 3.x.x.
* **Base de Datos:** MariaDB (contenedores Docker).
* **ORM:** Spring Data JPA / Hibernate.
* **Build Tool:** Maven (o Gradle, si se prefiere).
* **Contenerización:** Docker, Docker Compose.

## 🚀 Cómo Ponerlo en Marcha

Para levantar el laboratorio completo (aplicación y base de datos) usando Docker Compose, sigue estos pasos:

1.  **Requisitos Previos:**
    * [Docker Desktop](https://www.docker.com/products/docker-desktop) instalado y funcionando.
    * Java Development Kit (JDK) 21 instalado (necesario si quieres compilar desde código fuente, pero no para correr con Docker).
    * Un cliente Git.

2.  **Clonar el Repositorio:**
    ```bash
    git clone [https://github.com/CarolinaPereiraUY/BancoCentral-SecLand-Vulnerable.git](https://github.com/CarolinaPereiraUY/BancoCentral-SecLand-Vulnerable.git)
    cd BancoCentral-SecLand-Vulnerable
    ```
    (Ajusta el nombre de usuario de GitHub y el nombre del repositorio si son diferentes).

3.  **Configuración de la Base de Datos (MariaDB vía Docker Compose):**
    * Asegúrate de que el archivo `docker-compose.yaml` en la raíz de este proyecto esté configurado para levantar el servicio `bancocentral-db` (MariaDB). La configuración por defecto del `docker-compose.yaml` del proyecto ya lo incluye.
    * El script `src/main/resources/data.sql` se encargará de inicializar el esquema y los datos iniciales al arrancar la aplicación Spring Boot.

4.  **Construir la Imagen Docker y Levantar los Servicios:**
    * En la raíz del proyecto, ejecuta:
        ```bash
        docker compose up --build -d
        ```
    * Este comando construirá la imagen Docker de la aplicación (si hay un `Dockerfile`) y levantará los servicios (`bancocentral-app` y `bancocentral-db` o nombres similares).

5.  **Verificar el Estado:**
    * Puedes verificar el estado de los contenedores con:
        ```bash
        docker compose ps
        docker compose logs -f bancocentral-app # O el nombre de tu servicio de la app
        ```
    * Busca el mensaje `Application started` en los logs de la aplicación.

6.  **Acceder a la Aplicación:**
    * Una vez que la aplicación esté completamente iniciada, podrás acceder a ella vía web:
        * **Interfaz de Usuario (si existe):** `http://localhost:8080` (o el puerto configurado en `application.properties`).
        * **Endpoint de Prueba de API:** `http://localhost:8080/api/users` (ejemplo, ajusta según los endpoints reales que se construyan).

## 📄 Documentación Adicional

* **`VULNERABILITIES.md`**: Documento clave que detalla cada vulnerabilidad intencional, su propósito y cómo fue implementada.
* **`docker-compose.yaml`**: Configuración de los servicios Docker.
* **`application.properties`**: Configuraciones de Spring Boot y la base de datos.

---
**¡Advertencia!** Esta aplicación es deliberadamente vulnerable y está diseñada solo para fines educativos. No la uses en entornos de producción ni con datos reales.
---
