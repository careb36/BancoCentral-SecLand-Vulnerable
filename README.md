# BancoCentral-SecLand-Vulnerable - Laboratorio de Hacking Ético

Este repositorio contiene el código fuente de **"Banco Central de SecLand"**, una aplicación web de banca desarrollada con Spring Boot (Java) y PostgreSQL. El propósito principal de este proyecto es servir como un **laboratorio deliberadamente vulnerable** para la investigación y práctica de hacking ético, así como para el desarrollo de un módulo de detección de anomalías basado en Inteligencia Artificial, en el contexto de un Trabajo de Fin de Máster (TFM).

## Tabla de Contenidos
1. [Objetivo del Proyecto](#objetivo-del-proyecto)
2. [Características Implementadas](#características-implementadas)
3. [Vulnerabilidades y Prácticas de Seguridad](#vulnerabilidades-y-prácticas-de-seguridad)
4. [Tecnologías Utilizadas](#tecnologías-utilizadas)
5. [Cómo Ponerlo en Marcha](#cómo-ponerlo-en-marcha)
6. [Endpoints de la API](#endpoints-de-la-api)
7. [Datos de Ejemplo](#datos-de-ejemplo)
8. [Licencia](#licencia)

## Objetivo del Proyecto

El objetivo es proporcionar un entorno controlado para:
* Realizar pruebas de penetración (pentesting) sobre funcionalidades bancarias comunes desde **Kali Linux**.
* Estudiar y explotar vulnerabilidades de seguridad intencionalmente introducidas en el código y la lógica de negocio.
* Recolectar logs detallados para el entrenamiento y validación de un modelo de IA dedicado a la detección de ataques.
* Servir como una plataforma de investigación original para un TFM, garantizando la ausencia de "solucionarios" públicos para sus vulnerabilidades.

## Características Implementadas

* **Gestión de Usuarios:** Registro y autenticación de clientes.
* **Gestión de Cuentas:** Creación de cuentas de Ahorros y Corriente.
* **Transacciones:** Transferencias de fondos entre cuentas.
* **API RESTful:** Toda la funcionalidad está expuesta a través de una API REST.
* **Seguridad Mixta:** Combina prácticas de seguridad robustas con vulnerabilidades deliberadas.

## Vulnerabilidades y Prácticas de Seguridad

Esta aplicación ha sido diseñada con una postura de seguridad mixta para fines de estudio.

### Vulnerabilidades Intencionales

* **A05:2021-Broken Access Control (IDOR):** El endpoint de transferencias (`/api/accounts/transfer`) es vulnerable a **Referencia Directa Insegura a Objetos**. No valida que el usuario autenticado sea el propietario de la cuenta de origen (`sourceAccountId`), permitiendo a un atacante transferir fondos desde la cuenta de cualquier otro usuario con solo conocer su ID.
* **Fallo de Lógica de Negocio:** La misma funcionalidad de transferencia no valida si la cuenta de origen tiene saldo suficiente, lo que permite dejar las cuentas con saldo negativo.

### Prácticas Seguras Implementadas (Contraejemplos)

* **A02:2021-Cryptographic Failures:** Para prevenir la exposición de credenciales, el sistema **no almacena contraseñas en texto plano**. Todas las contraseñas se hashean utilizando el algoritmo **BCrypt** antes de ser guardadas en la base de datos, siguiendo las mejores prácticas de la industria.

## Tecnologías Utilizadas

* **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA.
* **Base de Datos:** PostgreSQL 15.
* **Build Tool:** Maven.
* **Contenerización:** Docker, Docker Compose.
* **Testing:** JUnit 5, Postman.
* **Plataforma de Ataque:** Kali Linux.

## Cómo Ponerlo en Marcha

El proyecto está 100% contenerizado para un despliegue fácil y rápido.

1.  **Requisitos Previos:**
    * [Docker Desktop](https://www.docker.com/products/docker-desktop) instalado y funcionando.
    * Un cliente Git.

2.  **Clonar y Ejecutar:**
    ```bash
    # Clona este repositorio
    git clone [https://github.com/careb36/BancoCentral-SecLand-Vulnerable.git](https://github.com/careb36/BancoCentral-SecLand-Vulnerable.git)

    # Entra en la carpeta del proyecto
    cd BancoCentral-SecLand-Vulnerable

    # Levanta la aplicación y la base de datos con Docker Compose
    # El comando --build asegura que se compile la última versión del código
    docker-compose up --build
    ```
    La aplicación estará disponible en `http://localhost:8080`.

## Endpoints de la API

### Autenticación (`/api/auth`)

* **Registrar un nuevo usuario**
    * **Endpoint:** `POST /api/auth/register`
    * **Body:**
        ```json
        {
            "username": "nuevo_usuario",
            "password": "una_clave_segura",
            "fullName": "Nombre Apellido"
        }
        ```

* **Iniciar Sesión**
    * **Endpoint:** `POST /api/auth/login`
    * **Body:**
        ```json
        {
            "username": "usuario_existente",
            "password": "su_clave"
        }
        ```

### Cuentas (`/api/accounts`)

* **Realizar una transferencia**
    * **Endpoint:** `POST /api/accounts/transfer`
    * **Body:**
        ```json
        {
            "sourceAccountId": 101,
            "destinationAccountId": 201,
            "amount": 500.00,
            "description": "Transferencia de prueba"
        }
        ```

## Datos de Ejemplo

La base de datos se inicializa con los siguientes usuarios y cuentas para facilitar las pruebas:

| Entidad | ID  | Detalles                                             |
| :------ | :-- | :--------------------------------------------------- |
| Usuario | 1   | `username`: **carolina_p**, `password`: **password123** |
| Usuario | 2   | `username`: **test_user**, `password`: **testpass** |
| Cuenta  | 101 | Tipo: Ahorros, Saldo: 5000.75, Dueño: `carolina_p`    |
| Cuenta  | 102 | Tipo: Corriente, Saldo: 1250.00, Dueño: `carolina_p`   |
| Cuenta  | 201 | Tipo: Ahorros, Saldo: 800.50, Dueño: `test_user`      |

## Licencia

Este proyecto se distribuye bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

---
**¡Advertencia!** Esta aplicación es deliberadamente vulnerable y está diseñada solo para fines educativos. No la uses en entornos de producción ni con datos reales.
---