# 🌍 Agencia de Viajes _WorldConnection_

Sistema de gestión profesional para agencias de viajes desarrollado en **Java SE**, utilizando una arquitectura de capas (**DAO/DTO**) y persistencia de datos en **MySQL**.

## 🚀 Funcionalidades del Sistema

- **Gestión Inteligente de Empleados:** Registro de personal con un sistema de ascenso automático:
- **Junior:** < 2 años de experiencia.
- **Senior:** 2 a 8 años de experiencia.
- **Coordinador:** > 8 años de experiencia.
- **Administración de Clientes:** Base de datos de viajeros con validación de integridad.
- **Catálogo de Destinos:** Organización de viajes por categorías (Cruceros, Aventura, Cultura, etc.).
- **Gestión de Reservas:** Vinculación relacional entre clientes, destinos y agentes responsables.
- **Validación Robusta:** Uso de la interfaz `Validable` y excepciones personalizadas (`AgenciaException`).

## 🛠️ Stack Tecnológico

- **Lenguaje:** Java 21 o superior.
- **IDE:** Eclipse Desktop IDE.
- **Base de Datos:** MySQL.
- **Conector:** MySQL Connector/J 8.4 (JDBC).
- **Modelado:** UML (Diagrama de Clases , Casos de Uso y Diagrama de Secuencia).

## 📂 Estructura de Paquetes (Eclipse)

El proyecto sigue una estructura modular para facilitar el mantenimiento:

- `Agencia_DTO`: Objetos de transferencia de datos y lógica de negocio (`EmpleadoDTO`, `ClienteDTO`).
- `Agencia_DAO`: Objetos de acceso a datos y sentencias SQL.
- `Agencia_Conexion`: Gestión de la sesión con el servidor MySQL.
- `Agencia_Excepciones`: Control de errores personalizado.
- `Agencia_Enums`: Definiciones para `Cargo` y `Turno`.
- `Agencia_Vista`: Interfaz gráfica de usuario desarrollada con Swing.

## 🗃️ Modelo de Datos

El sistema se apoya en una base de datos relacional con las siguientes entidades:
- `empleado`: Gestiona el rango y la experiencia del staff.
- `cliente`: Almacena información de contacto y pasaporte.
- `categoria`: Define los tipos de viaje disponibles.
- `destino`: Catálogo de ofertas turísticas.
- `reserva`: Relaciona todas las entidades anteriores.

## 📊 Estado del Proyecto: Sprint 1 (_En Desarrollo_)

Actualmente, el proyecto se encuentra en su fase inicial de arquitectura:
- [x] Diseño de Base de Datos y Script SQL.
- [x] Estructura de paquetes y arquitectura DAO/DTO.
- [ ] Lógica de negocio y validaciones en `EmpleadoDTO`.
- [ ] Implementación completa de Interfaces Gráficas (Swing).
- [ ] Conexión final de todos los módulos DAO con la base de datos.

*Este proyecto forma parte del Sprint 1 del módulo de Programación y Entornos de Desarrollo.*
*Próximo hito: Finalización del módulo de Reservas y Test unitarios.*
